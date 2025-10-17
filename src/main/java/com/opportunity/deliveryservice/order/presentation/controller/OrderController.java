package com.opportunity.deliveryservice.order.presentation.controller;

import java.util.UUID;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity.deliveryservice.global.common.response.ApiResponse;
import com.opportunity.deliveryservice.global.infrastructure.config.security.UserDetailsImpl;
import com.opportunity.deliveryservice.order.application.service.OrderService;
import com.opportunity.deliveryservice.order.presentation.dto.request.CancelOrderRequest;
import com.opportunity.deliveryservice.order.presentation.dto.request.CreateOrderRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	@Secured("ROLE_CUSTOMER")
	public ApiResponse<?> createOrder(
		@RequestBody CreateOrderRequest request,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		orderService.createOrder(request, userDetails.getUser());
		return ApiResponse.noContent();
	}

	@PostMapping("/{orderId}/cancel")
	@Secured({"ROLE_CUSTOMER", "ROLE_OWNER"}) // 주문자, 가게 주인 취소 가능
	public ApiResponse<?> cancelOrder(
		@PathVariable UUID orderId,
		@RequestBody CancelOrderRequest request,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		orderService.cancelOrder(orderId, request.cancelReason(), userDetails.getUser());
		return ApiResponse.noContent();
	}
}
