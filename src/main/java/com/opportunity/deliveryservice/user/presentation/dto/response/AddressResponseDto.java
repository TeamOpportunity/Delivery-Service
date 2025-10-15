package com.opportunity.deliveryservice.user.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.opportunity.deliveryservice.user.domain.entity.Address;

import lombok.Getter;

@Getter
public class AddressResponseDto {
	private UUID id;
	private String city;
	private String gu;
	private String detailAddress;
	private String nickname;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
	private LocalDateTime deletedAt;

	public AddressResponseDto(Address address) {
		this.id = address.getId();
		this.city = address.getCity();
		this.gu = address.getGu();
		this.detailAddress = address.getDetailAddress();
		this.nickname = address.getNickname();
		this.createdAt = address.getCreatedAt();
		this.modifiedAt = address.getModifiedAt();
		this.deletedAt = address.getDeletedAt();
	}
}

