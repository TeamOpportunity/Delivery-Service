package com.opportunity.deliveryservice.order.presentation.dto.request;

import java.util.UUID;

public record ChangeOrderProgressRequest(
	String orderProgress,
	UUID storeId
) {
}
