package com.opportunity.deliveryservice.user.presentation.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.global.common.response.ApiResponse;
import com.opportunity.deliveryservice.global.infrastructure.config.security.UserDetailsImpl;
import com.opportunity.deliveryservice.global.infrastructure.jwt.JwtUtil;
import com.opportunity.deliveryservice.user.application.service.AddressServiceV1;
import com.opportunity.deliveryservice.user.application.service.UserServiceV1;
import com.opportunity.deliveryservice.user.domain.entity.User;
import com.opportunity.deliveryservice.user.presentation.dto.request.AddressRequestDto;
import com.opportunity.deliveryservice.user.presentation.dto.request.AdminSearchUserRequestDto;
import com.opportunity.deliveryservice.user.presentation.dto.request.UserDeleteRequestDto;
import com.opportunity.deliveryservice.user.presentation.dto.request.UserRoleUpdateRequestDto;
import com.opportunity.deliveryservice.user.presentation.dto.request.UserSignupRequestDto;
import com.opportunity.deliveryservice.user.presentation.dto.request.UserUpdateRequestDto;
import com.opportunity.deliveryservice.user.presentation.dto.response.AddressResponseDto;
import com.opportunity.deliveryservice.user.presentation.dto.response.UserResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserControllerV1 {

	private final UserServiceV1 userService;
	private final AddressServiceV1 addressService;
	private final JwtUtil jwtUtil;

	/**
	 * 회원가입 API
	 */
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody UserSignupRequestDto requestDto) {
		userService.signup(requestDto);
		return ResponseEntity.ok(ApiResponse.successNoData("201 Created", "회원가입에 성공했습니다!"));
	}

	/**
	 * 로그인 API
	 * JwtAuthenticationFilter가 로그인 성공 or 실패 시 로직을 가지고 있음
	 * -> 로그인을 처리(Postman으로 테스트 성공)
	 */

	/**
	 * 로그아웃 API
	 * JWT Filter는 로그인 시에만 작동하므로, 로그아웃 시 토큰 폐기는 Controller/Service에서 수동 처리
	 */
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@CookieValue(value = JwtUtil.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		// JTI를 추출하기 위한 Access Token 유효성 검사
		String accessToken = JwtUtil.getJwtFromHeader(request);

		if (!StringUtils.hasText(accessToken)) {
			// Access Token이 없으면 인증되지 않은 요청
			throw new OpptyException(ClientErrorCode.UNAUTHORIZED);
		}

		// 2. Refresh Token 유효성 검사
		if (!StringUtils.hasText(refreshToken)) {
			// Refresh Token이 없으면 무효화 처리가 불가능
			throw new OpptyException(ClientErrorCode.INVALID_TOKEN);
		}

		// 3. 로그아웃 (AT/RT 블랙리스트 등록 및 Redis RT 삭제)
		userService.logout(accessToken, refreshToken);

		// 4. HttpOnly 쿠키 삭제
		jwtUtil.deleteCookie(response, JwtUtil.REFRESH_TOKEN_COOKIE_NAME);

		return ResponseEntity.ok(ApiResponse.successNoData("200 OK", "로그아웃에 성공했습니다"));
	}

	/**
	 * 토큰 재발급 API
	 * Refresh Token (Cookie)과 만료된 Access Token (Header)을 사용하여 새 토큰을 발급합니다.
	 */
	@PostMapping("/reissue")
	public ResponseEntity<ApiResponse<Void>> reissue(
		@CookieValue(value = JwtUtil.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		// 만료되었지만 JTI를 추출하기 위한 Access Token 유효성 검사
		String expiredAccessToken = JwtUtil.getJwtFromHeader(request);

		if (!StringUtils.hasText(expiredAccessToken)) {
			// Access Token가 없으면 재발급 요청 자체가 성립하지 않음
			throw new OpptyException(ClientErrorCode.UNAUTHORIZED);
		}
		if (!StringUtils.hasText(refreshToken)) {
			// Refresh Token 쿠키가 없으면 재발급 불가
			throw new OpptyException(ClientErrorCode.INVALID_TOKEN);
		}

		userService.reissueToken(expiredAccessToken, refreshToken, response);

		return ResponseEntity.ok(ApiResponse.success(null));
	}

	/**
	 * 마이페이지: 자신의 유저 정보 조회 (주소록 포함)
	 */
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserResponseDto>> getMyProfile(
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		User user = userService.findUserById(userDetails.getUser().getId());
		return ResponseEntity.ok(ApiResponse.success(new UserResponseDto(user)));
	}

	/**
	 * 회원 정보 수정
	 */
	@PutMapping("/me")
	public ResponseEntity<ApiResponse<Void>> updateProfile(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody UserUpdateRequestDto requestDto
	) {
		userService.updateUser(userDetails.getUser().getId(), requestDto, userDetails.getUser());
		return ResponseEntity.ok(ApiResponse.successNoData("200 OK", "회원정보 수정에 성공했습니다!"));
	}

	/**
	 * 회원 탈퇴 (Soft Delete)
	 */
	@DeleteMapping("/me")
	public ResponseEntity<ApiResponse<Void>> deleteUser(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestBody UserDeleteRequestDto requestDto
	) {
		userService.deleteUser(userDetails.getUser().getId(), requestDto.getPassword(), userDetails.getUser());
		return ResponseEntity.ok(ApiResponse.successNoData("200 OK", "성공적으로 회원탈퇴되었습니다!"));
	}

	// --- 주소록 (Address) CRUD ---

	/**
	 * 주소록 생성
	 */
	@PostMapping("/addresses")
	public ResponseEntity<ApiResponse<AddressResponseDto>> createAddress(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody AddressRequestDto requestDto
	) {
		addressService.createAddress(requestDto, userDetails.getUser());
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.successNoData("201 Created", "”주소록 등록에 성공했습니다!”"));
	}

	/**
	 * 주소록 수정
	 */
	@PutMapping("/addresses/{addressId}")
	public ResponseEntity<ApiResponse<AddressResponseDto>> updateAddress(
		@PathVariable UUID addressId,
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody AddressRequestDto requestDto
	) {
		addressService.updateAddress(addressId, requestDto, userDetails.getUser());
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.successNoData("200 OK", "”주소록 수정에 성공했습니다!”"));
	}

	/**
	 * 주소록 삭제 (Soft Delete)
	 */
	@DeleteMapping("/addresses/{addressId}")
	public ResponseEntity<ApiResponse<Void>> deleteAddress(
		@PathVariable UUID addressId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		addressService.deleteAddress(addressId, userDetails.getUser());
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.successNoData("200 OK", "”주소록 삭제가 완료되었습니다!”"));
	}

	// --- 관리자 전용 기능 (Admin) ---

	/**
	 * 관리자 전용: 모든 사용자 정보 조회 (Search/Paging/Sort)
	 * MASTER/MANAGER 권한만 접근 가능
	 */
	@Secured("ROLE_MASTER") // 권한이 MASTER만 접근 가능
	@GetMapping("/admin")
	public ResponseEntity<ApiResponse<Page<UserResponseDto>>> searchUsers(
		AdminSearchUserRequestDto requestDto // @RequestParam 파라미터가 DTO에 자동으로 바인딩
	) {
		Page<User> userPage = userService.searchUsersByAdmin(requestDto);

		// Entity Page를 DTO Page로 변환 (주소록도 포함)
		Page<UserResponseDto> responsePage = userPage.map(UserResponseDto::new);

		return ResponseEntity.ok(ApiResponse.success(responsePage));
	}

	/**
	 * 관리자 전용: 권한 변경 (MASTER 권한만 가능)
	 */
	@Secured("ROLE_MASTER") // 권한이 MASTER만 접근 가능
	@PatchMapping("/admin/{userId}/role")
	public ResponseEntity<ApiResponse<Void>> updateRole(
		@PathVariable Long userId,
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody UserRoleUpdateRequestDto requestDto
	) {
		// MASTER 권한 검증은 Service에서 수행
		userService.updateRole(userId, requestDto, userDetails.getUser());
		return ResponseEntity.ok(ApiResponse.successNoData("200 OK", "권한부여에 성공했습니다!"));
	}
}