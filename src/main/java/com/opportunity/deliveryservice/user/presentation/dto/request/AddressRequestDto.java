package com.opportunity.deliveryservice.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AddressRequestDto {
	@NotBlank(message = "도시를 입력해주세요.")
	private String city;

	@NotBlank(message = "구를 입력해주세요.")
	private String gu;

	@NotBlank(message = "상세 주소를 입력해주세요.")
	private String detailAddress;

	@NotBlank(message = "주소 별명을 입력해주세요.")
	private String nickname;
}
