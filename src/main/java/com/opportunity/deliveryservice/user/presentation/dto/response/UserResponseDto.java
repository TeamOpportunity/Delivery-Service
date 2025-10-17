package com.opportunity.deliveryservice.user.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.opportunity.deliveryservice.user.domain.entity.User;
import com.opportunity.deliveryservice.user.domain.entity.UserRoleEnum;

import lombok.Getter;

@Getter
public class UserResponseDto {
	private Long id;
	private String username;
	private String email;
	private UserRoleEnum role;
	private LocalDateTime createdAt;
	private LocalDateTime deletedAt;
	private List<AddressResponseDto> addressList;

	public UserResponseDto(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.role = user.getRole();
		this.createdAt = user.getCreatedAt();
		this.deletedAt = user.getDeletedAt();

		// 전체 주소 반환(UserServiceV1의 관리자 전용 조회 메서드(세선 필터)에서 제어)
		this.addressList = user.getAddressList().stream()
			// .filter(address -> address.getDeletedAt() == null)
			.map(AddressResponseDto::new)
			.collect(Collectors.toList());
	}
}
