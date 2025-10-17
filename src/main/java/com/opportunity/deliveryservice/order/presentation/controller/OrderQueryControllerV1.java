package com.opportunity.deliveryservice.order.presentation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity.deliveryservice.global.common.response.ApiResponse;
import com.opportunity.deliveryservice.global.infrastructure.config.security.UserDetailsImpl;
import com.opportunity.deliveryservice.order.application.service.OrderQueryServiceV1;
import com.opportunity.deliveryservice.order.presentation.dto.response.ResOrderDetailDto;
import com.opportunity.deliveryservice.order.presentation.dto.response.ResOrderListDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderQueryControllerV1 {
	private final OrderQueryServiceV1 orderQueryService;

	// 주문 내역 조회
	@GetMapping
	public ApiResponse<List<ResOrderListDto>> getOrders(@AuthenticationPrincipal UserDetailsImpl user) {
		List<ResOrderListDto> orderList = orderQueryService.getOrders(user);
		return ApiResponse.success(orderList);
	}

	// 주문 상세 내역 조회
	@GetMapping("{orderId}")
	public ApiResponse<ResOrderDetailDto> getOrderDetail(
		@PathVariable UUID orderId,
		@AuthenticationPrincipal UserDetailsImpl user
	) {
		ResOrderDetailDto detail = orderQueryService.getOrderDetail(orderId, user);
		return ApiResponse.success(detail);
	}
}
