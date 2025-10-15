package com.opportunity.deliveryservice.payment.domain.entity;

import java.time.LocalDateTime;
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

	@OneToOne
	Order order;

	@Column(nullable = false)
	Integer amount;

	@Enumerated(value = EnumType.STRING)
	TossPaymentStatus status;

	@Enumerated(value = EnumType.STRING)
	TossPaymentMethod method;

	String tossPaymentId;

	String tossOrderId;

	OffsetDateTime approvedAt;

	@Builder
	public Payment(Order order, Integer amount, String tossPaymentId, String tossOrderId, String status, String method, OffsetDateTime approvedAt) {
		this.order = order;
		this.amount = amount;
		this.tossPaymentId = tossPaymentId;
		this.tossOrderId = tossOrderId;
		this.status = TossPaymentStatus.from(status);
		this.method = TossPaymentMethod.from(method);
		this.approvedAt = approvedAt;
	}
}