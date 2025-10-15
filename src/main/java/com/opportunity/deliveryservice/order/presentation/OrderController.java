package com.opportunity.deliveryservice.order.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity.deliveryservice.global.common.response.ApiResponse;
import com.opportunity.deliveryservice.order.application.service.OrderService;
import com.opportunity.deliveryservice.order.presentation.dto.request.CreateOrderRequest;
import com.opportunity.deliveryservice.product.presentation.dto.request.CreateProductRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	public ApiResponse<?> createOrder(
		@RequestBody CreateOrderRequest request
		// @AuthenticationPrincipal
	) {
		orderService.createOrder(request);
		return ApiResponse.noContent();
	}
}
