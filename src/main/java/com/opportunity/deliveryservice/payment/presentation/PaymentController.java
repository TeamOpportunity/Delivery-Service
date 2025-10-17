package com.opportunity.deliveryservice.payment.presentation;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity.deliveryservice.global.common.response.ApiResponse;
import com.opportunity.deliveryservice.global.infrastructure.config.security.UserDetailsImpl;
import com.opportunity.deliveryservice.payment.application.service.PaymentService;
import com.opportunity.deliveryservice.payment.presentation.dto.request.CancelPaymentRequest;
import com.opportunity.deliveryservice.payment.presentation.dto.request.ConfirmPaymentRequest;
import com.opportunity.deliveryservice.payment.presentation.dto.request.IntentPaymentRequest;
import com.opportunity.deliveryservice.payment.presentation.dto.response.PaymentResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
@Tag(name = "Payment", description = "결제 API")
public class PaymentController {

	private final PaymentService paymentService;

	@Secured("ROLE_CUSTOMER")
	@PostMapping("/intents/{orderId}")
	public ApiResponse<?> intentPayment(
		@PathVariable UUID orderId,
		@RequestBody IntentPaymentRequest request,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		paymentService.intentPayment(orderId, request, userDetails.getUser());
		return ApiResponse.noContent();
	}

	@Secured("ROLE_CUSTOMER")
	@PostMapping("/confirm")
	public ApiResponse<?> getPaymentInfo(
		@RequestBody ConfirmPaymentRequest request,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		paymentService.confirmPayment(request, userDetails.getUser());
		return ApiResponse.noContent();
	}

	@Secured("ROLE_CUSTOMER")
	@PostMapping("/cancel")
	public ApiResponse<?> cancelPayment(
		@RequestBody CancelPaymentRequest request,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		paymentService.cancelPayment(request.paymentKey(), request.cancelReason(), userDetails.getUser());
		return ApiResponse.noContent();
	}

	@Secured("ROLE_CUSTOMER")
	@GetMapping("/{orderId}")
	public ApiResponse<PaymentResponse> getPaymentInfo(
		@PathVariable UUID orderId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		return ApiResponse.success(paymentService.getPayment(orderId, userDetails.getUser()));
	}
}

