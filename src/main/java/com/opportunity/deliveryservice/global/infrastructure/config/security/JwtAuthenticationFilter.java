package com.opportunity.deliveryservice.global.infrastructure.config.security;

import java.io.IOException;
import java.time.Duration;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.response.ApiResponse;
import com.opportunity.deliveryservice.global.infrastructure.jwt.JwtUtil;
import com.opportunity.deliveryservice.global.infrastructure.redis.RedisService;
import com.opportunity.deliveryservice.user.domain.entity.User;
import com.opportunity.deliveryservice.user.domain.entity.UserRoleEnum;
import com.opportunity.deliveryservice.user.presentation.dto.request.UserLoginRequestDto;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final JwtUtil jwtUtil;
	private final RedisService redisService;

	public JwtAuthenticationFilter(JwtUtil jwtUtil, RedisService redisService) {
		this.jwtUtil = jwtUtil;
		this.redisService = redisService;
		setFilterProcessesUrl("/v1/users/login");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {
		try {
			UserLoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(),
				UserLoginRequestDto.class);

			return getAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken(
					requestDto.getUsername(),
					requestDto.getPassword(),
					null
				)
			);
		} catch (IOException e) {
			log.error("인증 시도 중 입력 오류: {}", e.getMessage());
			throw new RuntimeException("로그인 요청 처리 실패", e);
		}
	}

	//로그인 성공 시 실행
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) {
		User user = ((UserDetailsImpl)authResult.getPrincipal()).getUser();
		String username = user.getUsername();
		UserRoleEnum role = user.getRole();

		// 이전 토큰 전부 무효화
		invalidatePreviousTokens(request, username);

		// 1. Access Token 생성 및 Header에 저장 (30분, Header)
		String accessToken = jwtUtil.createAccessToken(username, role);
		response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);

		// 2. Refresh Token 생성 (14일)
		String refreshToken = jwtUtil.createRefreshToken(username, role);

		// 3. Refresh Token을 HttpOnly 쿠키에 저장 (7일)
		Cookie refreshTokenCookie = jwtUtil.createRefreshTokenCookie(refreshToken);
		response.addCookie(refreshTokenCookie);

		// 4. Refresh Token을 Redis에 저장 (14일 TTL)
		long rtExpirationMs = jwtUtil.getExpirationRemainingTime(refreshToken);
		redisService.setRefreshToken(username, refreshToken, Duration.ofMillis(rtExpirationMs));

		log.info("로그인 성공: AT, RT 발급 및 Redis 저장 완료. User: {}", username);
	}

	private void invalidatePreviousTokens(HttpServletRequest request, String username) {

		// 1. 이전 Access Token 무효화 (헤더에서 추출)
		String previousAccessToken = JwtUtil.getJwtFromHeader(request);
		if (StringUtils.hasText(previousAccessToken)) {
			try {
				// 이전 AT의 JTI를 추출하고 블랙리스트에 등록
				String jti = jwtUtil.getJtiFromToken(previousAccessToken);
				long ttl = jwtUtil.getExpirationRemainingTime(previousAccessToken);
				if (ttl > 0) {
					redisService.setBlacklist(jti, Duration.ofMillis(ttl));
					log.warn("새 로그인 성공: 이전 Access Token 블랙리스트 등록 완료. JTI: {}", jti);
				}
			} catch (Exception e) {
				// AT가 이미 만료되었거나 유효하지 않아 JTI 추출에 실패하면 무시
				log.debug("이전 Access Token 무효화 중 오류 발생 (이미 만료 또는 손상): {}", e.getMessage());
			}
		}

		// 2. 이전 Refresh Token 무효화 (쿠키에서 추출 및 JTI 블랙리스트 등록)
		String previousRefreshToken = getRefreshTokenFromCookie(request);

		if (StringUtils.hasText(previousRefreshToken)) {
			try {
				// 이전 RT의 JTI를 추출하고 블랙리스트에 등록
				String jti = jwtUtil.getJtiFromToken(previousRefreshToken);
				long ttl = jwtUtil.getExpirationRemainingTime(previousRefreshToken);
				if (ttl > 0) {
					redisService.setBlacklist(jti, Duration.ofMillis(ttl));
					log.warn("새 로그인 성공: 이전 Refresh Token 블랙리스트 등록 완료. JTI: {}", jti);
				}
			} catch (Exception e) {
				// RT가 이미 만료되었거나 유효하지 않아 JTI 추출에 실패하면 무시
				log.debug("이전 Refresh Token 무효화 중 오류 발생 (이미 만료 또는 손상): {}", e.getMessage());
			}
		}
	}

	/**
	 * 요청 쿠키에서 Refresh Token 값을 추출하는 헬퍼 메서드
	 */
	private String getRefreshTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(JwtUtil.REFRESH_TOKEN_COOKIE_NAME)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	// 로그인 실패 시 실행
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException {
		log.error("로그인 실패: {}", failed.getMessage());
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(new ObjectMapper().writeValueAsString(
			new ApiResponse<>(
				ClientErrorCode.INVALID_PASSWORD.getCode(),
				ClientErrorCode.INVALID_PASSWORD.getMessage(),
				null
			)
		));
	}
}