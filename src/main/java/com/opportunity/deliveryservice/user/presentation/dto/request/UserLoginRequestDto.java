package com.opportunity.deliveryservice.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserLoginRequestDto {
	@NotBlank(message = "아이디를 입력해주세요.")
	private String username;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	private String password;
}