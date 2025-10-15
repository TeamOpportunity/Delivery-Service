package com.opportunity.deliveryservice.cart.presentation.dto.response;

import java.util.UUID;

import com.opportunity.deliveryservice.cart.domain.entity.CartProducts;
import com.opportunity.deliveryservice.product.domain.entity.Product;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResCartProductsDto {
	private UUID productId;
	private String title;
	private Long price;
	private Long quantity;
	private String image;
	//private UUID storeId;

	// 엔티티를 Dto로 변환
	public static ResCartProductsDto fromEntity(CartProducts cartProducts) {
		Product product = cartProducts.getProduct();
		return 	ResCartProductsDto.builder()
			.productId(product.getId())
			.title(product.getTitle())
			.price(product.getPrice())
			.quantity(cartProducts.getQuantity())
			.image(product.getImage())
			.build();
	}
}
