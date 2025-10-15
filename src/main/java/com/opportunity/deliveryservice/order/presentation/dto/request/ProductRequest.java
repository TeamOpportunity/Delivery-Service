package com.opportunity.deliveryservice.order.presentation.dto.request;

import java.util.UUID;

public record ProductRequest(
	UUID productId,
	Long quantity
) {
}
