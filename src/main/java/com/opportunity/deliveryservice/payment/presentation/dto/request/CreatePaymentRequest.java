package com.opportunity.deliveryservice.payment.presentation.dto.request;

import java.util.UUID;

public record CreatePaymentRequest(
	Integer amount,
	String tossPaymentKey,
	String tossOrderId,
	UUID orderId

) {
}
