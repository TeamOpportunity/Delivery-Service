package com.opportunity.deliveryservice.payment.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity.deliveryservice.global.common.response.ApiResponse;
import com.opportunity.deliveryservice.payment.application.service.PaymentService;
import com.opportunity.deliveryservice.payment.presentation.dto.request.CreatePaymentRequest;
import com.opportunity.deliveryservice.product.presentation.dto.request.CreateProductRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	public ApiResponse<?> confirmPayment(
		@RequestBody CreatePaymentRequest request
		// @AuthenticationPrincipal
	) {
		paymentService.confirmPayment(request);
		return ApiResponse.noContent();
	}
}

