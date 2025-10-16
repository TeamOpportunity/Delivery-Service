package com.opportunity.deliveryservice.payment.domain.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.opportunity.deliveryservice.global.common.entity.BaseEntity;
import com.opportunity.deliveryservice.order.domain.entity.Order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

	@GeneratedValue
	@Id
	UUID id;

	@Column(nullable = false)
	Integer amount;

	@Enumerated(value = EnumType.STRING)
	PaymentStatus status;

	@Enumerated(value = EnumType.STRING)
	TossPaymentMethod method;

	String tossPaymentId;

	String tossOrderId;

	OffsetDateTime approvedAt;

	@OneToOne
	Order order;

	String errorCode;
	String errorMessage;

	@Builder
	public Payment(Integer amount, Order order) {
		this.amount = amount;
		this.order = order;
		this.status = PaymentStatus.PAYMENT_PENDING;
	}

	public void setPaymentInfo(String tossPaymentId, String tossOrderId){
		this.tossPaymentId = tossPaymentId;
		this.tossOrderId = tossOrderId;
	}

	public void setPaymentResult(String status, String method, OffsetDateTime approvedAt){
		this.status = PaymentStatus.from(status);
		this.method = TossPaymentMethod.from(method);
		this.approvedAt = approvedAt;
	}

	public void setPaymentError(String errorCode, String errorMessage){
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}