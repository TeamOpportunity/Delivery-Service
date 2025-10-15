package com.opportunity.deliveryservice.order.domain.entity;

import java.util.UUID;

import org.antlr.v4.runtime.misc.NotNull;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

	@GeneratedValue
	@Id
	UUID id;

	// User user;

	@Enumerated(value = EnumType.STRING)
	OrderProgress progress = OrderProgress.ORDER_REQUESTED;

	@Column(nullable = false)
	Integer amount;

	String request;

	@Builder
	public Order(Integer amount, String request) { //todo - user 추가
		this.amount = amount;
		this.request = request;
	}

	public void changeProgress(OrderProgress progress){
		this.progress = progress;
	}
}
