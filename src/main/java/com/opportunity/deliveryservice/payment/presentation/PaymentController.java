package com.opportunity.deliveryservice.payment.presentation;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity.deliveryservice.global.common.response.ApiResponse;
import com.opportunity.deliveryservice.payment.application.service.PaymentService;
import com.opportunity.deliveryservice.payment.presentation.dto.request.ConfirmPaymentRequest;
import com.opportunity.deliveryservice.payment.presentation.dto.request.IntentPaymentRequest;
import com.opportunity.deliveryservice.payment.presentation.dto.response.PaymentResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping("/intents/{orderId}")
	public ApiResponse<?> intentPayment(
		@PathVariable UUID orderId,
		@RequestBody IntentPaymentRequest request
		// @AuthenticationPrincipal
	) {
		paymentService.intentPayment(orderId, request);
		return ApiResponse.noContent();
	}

	@PostMapping("/confirm")
	public ApiResponse<?> confirmPayment(
		@RequestBody ConfirmPaymentRequest request
		// @AuthenticationPrincipal
	) {
		paymentService.confirmPayment(request);
		return ApiResponse.noContent();
	}

	@GetMapping("/{orderId}")
	public ApiResponse<PaymentResponse> confirmPayment(
		@PathVariable UUID orderId
		// @AuthenticationPrincipal
	) {
		return ApiResponse.success(paymentService.getPayment(orderId));
	}
}

