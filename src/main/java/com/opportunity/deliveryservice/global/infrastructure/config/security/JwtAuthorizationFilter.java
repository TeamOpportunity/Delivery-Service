package com.opportunity.deliveryservice.global.infrastructure.config.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.response.ApiResponse;
import com.opportunity.deliveryservice.global.infrastructure.jwt.JwtUtil;
import com.opportunity.deliveryservice.global.infrastructure.redis.RedisService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
	private final RedisService redisService; // 1. RedisService 추가 주입

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws
		ServletException,
		IOException {

		String tokenValue = JwtUtil.getJwtFromHeader(req);

		if (StringUtils.hasText(tokenValue)) {
			try {
				// 1. 토큰 검증 (만료되면 ExpiredJwtException 발생)
				jwtUtil.validateToken(tokenValue);

				// 로그아웃 토큰 사용 방지를 위한 블랙리스트 검사 로직 추가
				// 1-1. Access Token의 JTI(JWT ID) 추출
				String jti = jwtUtil.getJtiFromToken(tokenValue);

				// 1-2. Redis 블랙리스트에서 해당 JTI 존재 여부 확인
				if (redisService.isTokenBlacklisted(jti)) { // RedisService에 있는 isTokenBlacklisted 사용
					log.error("블랙리스트에 등록된 토큰 사용 시도: {}", jti);
					// SecurityException을 발생시켜 하단의 catch 블록으로 넘깁니다.
					throw new SecurityException("Blacklisted Access Token");
				}

				// 2. Claim에서 사용자 정보 추출 및 SecurityContext 설정
				Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
				setAuthentication(info.getSubject());

			} catch (ExpiredJwtException e) {
				log.warn("Access Token 만료: {} - 재발급 필요", e.getMessage());
				handleSecurityException(res, ClientErrorCode.EXPIRED_TOKEN);
				return;
			} catch (SecurityException e) { // 블랙리스트 또는 유효하지 않은 토큰
				log.error("블랙리스트 토큰 또는 유효하지 않은 토큰: {}", e.getMessage());
				handleSecurityException(res, ClientErrorCode.BLACKLISTED_TOKEN);
				return;
			} catch (Exception e) {
				log.error("JWT 검증 중 알 수 없는 오류 발생: {}", e.getMessage());
				handleSecurityException(res, ClientErrorCode.INVALID_TOKEN);
				return;
			}
		}

		filterChain.doFilter(req, res);
	}

	/**
	 * SecurityContext에 인증 정보 설정
	 */
	public void setAuthentication(String username) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
			userDetails.getAuthorities());
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	/**
	 * 보안 관련 예외 발생 시 응답 처리 (401 UNAUTHORIZED)
	 */
	private void handleSecurityException(HttpServletResponse res, ClientErrorCode errorCode) throws IOException {
		res.setStatus(errorCode.getHttpStatus().value());
		res.setContentType("application/json;charset=UTF-8");
		res.getWriter().write(new ObjectMapper().writeValueAsString(
			ApiResponse.fail(errorCode.getCode(), errorCode.getMessage())
		));
	}
}