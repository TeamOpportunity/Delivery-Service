package com.opportunity.deliveryservice.user.presentation.dto.request;

import com.opportunity.deliveryservice.user.domain.entity.UserRoleEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserSignupRequestDto {
	@Pattern(regexp = "^[a-z0-9]{4,10}$", message = "아이디는 4~10자의 소문자와 숫자로만 구성되어야 합니다.")
	@NotBlank
	private String username;

	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$", message = "비밀번호는 8~15자의 대소문자, 숫자, 특수문자로 구성되어야 합니다.")
	@NotBlank
	private String password;

	@Email(message = "유효하지 않은 이메일 형식입니다.")
	@NotBlank
	private String email;

	private UserRoleEnum role = UserRoleEnum.CUSTOMER; // 기본권한 CUSTOMER

	private String roleAuthKey; // MASTER 권한 부여 시 사용할 인증 키
}
