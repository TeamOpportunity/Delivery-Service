package com.opportunity.deliveryservice.order.presentation.dto.request;

import java.util.List;
import java.util.UUID;

import com.opportunity.deliveryservice.product.domain.entity.Product;

public record CreateOrderRequest(
	Long storeId,
	Integer amount,
	String request,
	List<ProductRequest> productList
) {
}
