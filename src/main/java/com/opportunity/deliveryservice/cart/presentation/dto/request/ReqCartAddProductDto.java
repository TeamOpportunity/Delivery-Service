package com.opportunity.deliveryservice.cart.presentation.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqCartAddProductDto {
	private UUID productId;
	private Long quantity;
}
