package com.opportunity.deliveryservice.order.presentation.dto.response;

import java.util.UUID;

import com.opportunity.deliveryservice.order.domain.entity.OrderProduct;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResOrderProductDto {
	private UUID productId;
	private String productTitle;
	private Long productPrice;
	private Long productQuantity;
	private String productImage;
	private UUID storeId;
	private String storeName;

	public static ResOrderProductDto fromEntity(OrderProduct orderProduct) {
		return ResOrderProductDto.builder()
			.productId(orderProduct.getProductId())
			.productTitle(orderProduct.getProductTitle())
			.productPrice(orderProduct.getProductPrice())
			.productQuantity(orderProduct.getProductQuantity())
			.productImage(orderProduct.getProductImage())
			.storeId(orderProduct.getStoreId())
			.storeName(orderProduct.getStoreName())
			.build();
	}
}
