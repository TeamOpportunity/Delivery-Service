package com.opportunity.deliveryservice.user.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserUpdateRequestDto {
	private String oldPassword;

	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
		message = "비밀번호는 8~15자의 대소문자, 숫자, 특수문자로 구성되어야 합니다.")
	private String newPassword;

	@Email(message = "유효하지 않은 이메일 형식입니다.")
	private String email;
}