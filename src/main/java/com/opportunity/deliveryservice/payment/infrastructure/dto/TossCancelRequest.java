package com.opportunity.deliveryservice.payment.infrastructure.dto;

public record TossCancelRequest(
	String cancelReason
) {
	public static TossCancelRequest of(String cancelReason){
		return new TossCancelRequest(cancelReason);
	}
}
