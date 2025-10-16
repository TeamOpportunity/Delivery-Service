package com.opportunity.deliveryservice.payment.presentation.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.opportunity.deliveryservice.payment.infrastructure.dto.TossConfirmResponse;

public record PaymentResponse(
	Integer amount,
	String paymentMethod,
	String status,
	UUID paymentId,
	String orderName,
	OffsetDateTime approvedAt
) {
	public static PaymentResponse of(TossConfirmResponse response, UUID paymentId){
		return new PaymentResponse(response.getTotalAmount(), response.getMethod(), response.getStatus(), paymentId,
			response.getOrderName(), response.getApprovedAt());
	}
}
