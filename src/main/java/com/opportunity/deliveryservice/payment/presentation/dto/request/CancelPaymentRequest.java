package com.opportunity.deliveryservice.payment.presentation.dto.request;

public record CancelPaymentRequest(
	String paymentKey,
	String cancelReason
) {
}
