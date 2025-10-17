package com.opportunity.deliveryservice.cart.presentation.dto.response;

import java.util.List;
import java.util.UUID;

import com.opportunity.deliveryservice.cart.domain.entity.Cart;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResCartGetByUserDto {
	private UUID cartId;
	private Long userId;
	private List<ResCartProductsDto> cartProducts;

	// 엔티티를 Dto로 변환
	public static ResCartGetByUserDto fromEntity(Cart cart, List<ResCartProductsDto> cartProducts) {
		return ResCartGetByUserDto.builder()
			.cartId(cart.getId())
			.userId(cart.getUserId())
			.cartProducts(cartProducts)
			.build();
	}
}
