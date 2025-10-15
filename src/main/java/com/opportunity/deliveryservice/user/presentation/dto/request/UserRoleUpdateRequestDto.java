package com.opportunity.deliveryservice.user.presentation.dto.request;

import com.opportunity.deliveryservice.user.domain.entity.UserRoleEnum;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserRoleUpdateRequestDto {
	@NotNull(message = "변경할 권한을 반드시 입력해주세요.")
	private UserRoleEnum newRole;
}
