package com.opportunity.deliveryservice.payment.presentation.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.opportunity.deliveryservice.payment.infrastructure.dto.TossPaymentResponse;

public record PaymentResponse(
	Integer amount,
	String paymentMethod,
	String status,
	UUID paymentId,
	String paymentKey,
	String orderName,
	OffsetDateTime approvedAt
) {
	public static PaymentResponse of(TossPaymentResponse response, UUID paymentId){
		return new PaymentResponse(response.getTotalAmount(), response.getMethod(), response.getStatus(), paymentId, response.getPaymentKey(),
			response.getOrderName(), response.getApprovedAt());
	}
}
