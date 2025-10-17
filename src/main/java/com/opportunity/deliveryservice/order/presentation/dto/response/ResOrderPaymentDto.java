package com.opportunity.deliveryservice.order.presentation.dto.response;

import com.opportunity.deliveryservice.payment.domain.entity.Payment;
import com.opportunity.deliveryservice.payment.domain.entity.PaymentStatus;
import com.opportunity.deliveryservice.payment.domain.entity.TossPaymentMethod;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResOrderPaymentDto {
	private TossPaymentMethod method;
	private Integer amount;
	private PaymentStatus status;

	public static ResOrderPaymentDto fromEntity(Payment payment) {
		if (payment == null) return null; // 결제가 없는 주문 null 허용
		return ResOrderPaymentDto.builder()
			.method(payment.getMethod())
			.amount(payment.getAmount())
			.status(payment.getStatus())
			.build();
	}
}
