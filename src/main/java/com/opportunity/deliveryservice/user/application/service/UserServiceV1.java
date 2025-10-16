package com.opportunity.deliveryservice.user.application.service;

import java.time.Duration;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.global.infrastructure.jwt.JwtUtil;
import com.opportunity.deliveryservice.global.infrastructure.redis.RedisService;
import com.opportunity.deliveryservice.user.domain.entity.User;
import com.opportunity.deliveryservice.user.domain.entity.UserRoleEnum;
import com.opportunity.deliveryservice.user.domain.repository.UserRepository;
import com.opportunity.deliveryservice.user.presentation.dto.request.AdminSearchUserRequestDto;
import com.opportunity.deliveryservice.user.presentation.dto.request.UserRoleUpdateRequestDto;
import com.opportunity.deliveryservice.user.presentation.dto.request.UserSignupRequestDto;
import com.opportunity.deliveryservice.user.presentation.dto.request.UserUpdateRequestDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceV1 {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final RedisService redisService;
	private final EntityManager entityManager;

	@Value("${jwt.admin.token}")
	private String ADMIN_TOKEN;

	/**
	 * 회원가입
	 */
	@Transactional
	public void signup(UserSignupRequestDto requestDto) {
		// 중복 검사
		if (userRepository.existsByUsername(requestDto.getUsername())) {
			throw new OpptyException(ClientErrorCode.DUPLICATE_USERNAME);
		}
		if (userRepository.existsByEmail((requestDto.getEmail()))) {
			throw new OpptyException(ClientErrorCode.DUPLICATE_EMAIL);
		}

		// 권한 검사 (MASTER 권한은 인증키 필요)
		UserRoleEnum role = requestDto.getRole();
		if (role == UserRoleEnum.MASTER) {
			if (!ADMIN_TOKEN.equals(requestDto.getRoleAuthKey())) {
				throw new OpptyException(ClientErrorCode.INVALID_ADMIN_KEY);
			}
		}

		// 비밀번호 암호화
		String rawPassword = requestDto.getPassword();
		String encodedPassword = passwordEncoder.encode(rawPassword);

		// MANAGER는 MASTER가 권한 부여해야 하므로 회원가입 X
		if (role != UserRoleEnum.MANAGER || !ADMIN_TOKEN.equals(requestDto.getRoleAuthKey())) {
			// 사용자 등록: MANAGER는 MASTER가 권한 부여해줘야 가능

			User user = new User(requestDto, encodedPassword);

			userRepository.save(user);
		} else {
			throw new OpptyException(ClientErrorCode.INVALID_ADMIN_KEY);
		}
	}

	/**
	 * 로그아웃 (AT/RT 블랙리스트 등록 및 Redis RT 삭제)
	 */
	@Transactional
	public void logout(String accessToken, String refreshToken) {

		// Access Token 유효성 검사 (만료 여부, 서명 등)
		// Refresh Token 유효성 검사 (만료 여부, 서명 등)
		try {
			jwtUtil.validateToken(accessToken); // 여기서 블랙리스트 및 만료 검사
			jwtUtil.validateToken(refreshToken); // 여기서 블랙리스트 및 만료 검사
		} catch (ExpiredJwtException e) {
			throw new OpptyException(ClientErrorCode.EXPIRED_TOKEN);
		} catch (SecurityException e) {
			throw new OpptyException(ClientErrorCode.BLACKLISTED_TOKEN);
		} catch (Exception e) {
			throw new OpptyException(ClientErrorCode.INVALID_TOKEN);
		}

		// 1. Access Token Blacklist 등록 (AT의 JTI 사용)
		String atJti = jwtUtil.getJtiFromToken(accessToken);
		long atExpirationMs = jwtUtil.getExpirationRemainingTime(accessToken);
		if (atExpirationMs > 0) {
			redisService.setBlacklist(atJti, Duration.ofMillis(atExpirationMs));
		}

		// 2. Refresh Token Blacklist 등록 (RT의 JTI 사용)
		String rtJti = jwtUtil.getJtiFromToken(refreshToken);
		long rtExpirationMs = jwtUtil.getExpirationRemainingTime(refreshToken);
		if (rtExpirationMs > 0) {
			redisService.setBlacklist(rtJti, Duration.ofMillis(rtExpirationMs));
		}

		// 3. Redis에 저장된 Refresh Token 삭제 & HttpOnly 쿠키 만료
		Claims rtClaims = jwtUtil.getUserInfoFromToken(refreshToken); // username으로 삭제
		redisService.deleteRefreshToken(rtClaims.getSubject());
	}

	/**
	 * 토큰 재발급 (RT 유효성 검사 -> 기존 토큰 폐기 -> 새 토큰 발급 및 저장)
	 */
	@Transactional
	public void reissueToken(String accessToken, String refreshToken, HttpServletResponse response) {
		// 1. Refresh Token 유효성 검사 (만료 여부, 서명 등)
		try {
			jwtUtil.validateToken(refreshToken); // 여기서 블랙리스트 및 만료 검사
		} catch (ExpiredJwtException e) {
			throw new OpptyException(ClientErrorCode.EXPIRED_TOKEN);
		} catch (SecurityException e) {
			throw new OpptyException(ClientErrorCode.BLACKLISTED_TOKEN);
		} catch (Exception e) {
			throw new OpptyException(ClientErrorCode.INVALID_TOKEN);
		}

		// 2. Refresh Token Claims 추출 및 Redis 일치 확인
		Claims rtClaims = jwtUtil.getUserInfoFromToken(refreshToken);
		String username = rtClaims.getSubject();
		String storedRt = redisService.getRefreshToken(username);

		if (storedRt == null || !storedRt.equals(refreshToken)) {
			throw new OpptyException(ClientErrorCode.BLACKLISTED_TOKEN);
		}

		// 3. 기존 AT/RT Blacklist 등록 및 Redis RT 삭제 (로그아웃 로직 재사용)
		logout(accessToken, refreshToken);

		// 4. 새로운 Access Token, Refresh Token 생성
		UserRoleEnum role = UserRoleEnum.valueOf(rtClaims.get(JwtUtil.AUTHORIZATION_KEY, String.class).substring(5));
		String newAccessToken = jwtUtil.createAccessToken(username, role);
		String newRefreshToken = jwtUtil.createRefreshToken(username, role);

		// 5. 새로운 토큰 응답 설정
		response.addHeader(JwtUtil.AUTHORIZATION_HEADER, newAccessToken);
		response.addCookie(jwtUtil.createRefreshTokenCookie(newRefreshToken));

		// 6. 새로운 Refresh Token Redis에 저장
		long newRtExpirationMs = jwtUtil.getExpirationRemainingTime(newRefreshToken);
		redisService.setRefreshToken(username, newRefreshToken, Duration.ofMillis(newRtExpirationMs));
	}

	/**
	 * 유저 조회
	 */
	public User findUserById(Long userId) {

		// Soft Delete 안된 정보만 조회
		Session session = entityManager.unwrap(Session.class);
		Filter addressFilter = session.enableFilter("deletedAddressFilter");
		addressFilter.setParameter("isDeleted", false);

		return userRepository.findById(userId).orElseThrow(() ->
			new OpptyException(ClientErrorCode.USER_NOT_FOUND)
		);
	}

	/**
	 * 회원 정보 수정
	 */
	@Transactional
	public void updateUser(Long userId, UserUpdateRequestDto requestDto, User currentUser) {
		User user = findUserById(userId);

		// 1. 권한 검증: 본인만 수정 가능
		if (!user.getId().equals(currentUser.getId())) {
			throw new OpptyException(ClientErrorCode.FORBIDDEN);
		}

		String encodedNewPassword = null;
		if (requestDto.getNewPassword() != null) {
			// 기존 비밀번호 확인
			if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())) {
				throw new OpptyException(ClientErrorCode.INVALID_PASSWORD);
			}
			encodedNewPassword = passwordEncoder.encode(requestDto.getNewPassword());
		}

		// 2. 정보 업데이트
		user.update(requestDto, encodedNewPassword);
	}

	/**
	 * 회원 탈퇴 (Soft Delete)
	 */
	@Transactional
	public void deleteUser(Long userId, String password, User currentUser) {
		User user = findUserById(userId);

		// 1. 권한 검증: 본인만 탈퇴 가능
		if (!user.getId().equals(currentUser.getId())) {
			throw new OpptyException(ClientErrorCode.FORBIDDEN);
		}

		// 2. 비밀번호 확인
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new OpptyException(ClientErrorCode.INVALID_PASSWORD);
		}

		// 3. Soft Delete 실행 (@SQLDelete에 의해 deleted_at 업데이트)
		userRepository.delete(user);
	}

	/**
	 * 관리자 전용 사용자 조회 (검색, 페이징, 정렬, Soft Delete 포함)
	 */
	public Page<User> searchUsersByAdmin(AdminSearchUserRequestDto requestDto) {

		Session session = entityManager.unwrap(Session.class);
		// User 필터 활성화 - 삭제된 User도 포함하여 조회
		Filter userFilter = session.enableFilter("deletedUserFilter");
		userFilter.setParameter("isDeleted", true);
		// Address 필터 활성화 - 삭제된 Address도 포함하여 조회
		Filter addressFilter = session.enableFilter("deletedAddressFilter");
		addressFilter.setParameter("isDeleted", true);
		Pageable pageable = requestDto.toPageable();
		String keyword = requestDto.getKeyword();
		UserRoleEnum role = requestDto.getRole();
		try {
			// 키워드에 와일드카드 문자열 "%"를 추가하여 JPQL LIKE 연산에 전달
			if (keyword != null) {
				keyword = "%" + keyword + "%";
			}
			// 모든 데이터를 조회하는 Custom Repository 메서드 사용
			Page<User> userPage = userRepository.findUsersByAdminCriteria(role, keyword, pageable);
			return userPage;
		} finally {
			// 필터 비활성화
			session.disableFilter("deletedUserFilter");
			session.disableFilter("deletedAddressFilter");
		}

	}

	/**
	 * 관리자에 의한 권한 부여/변경 (MASTER만 가능)
	 */
	@Transactional
	public void updateRole(Long targetUserId, UserRoleUpdateRequestDto requestDto, User currentUser) {
		// 1. 현재 사용자(Master)의 권한 확인 (Master만 권한 변경 가능)
		if (currentUser.getRole() != UserRoleEnum.MASTER) {
			throw new OpptyException(ClientErrorCode.UNAUTHORIZED_ROLE_CHANGE);
		}

		User targetUser = userRepository.findById(targetUserId).orElseThrow(() ->
			new OpptyException(ClientErrorCode.USER_NOT_FOUND)
		);

		// 2. 권한 변경
		targetUser.updateRole(requestDto.getNewRole());
	}
}
