package com.opportunity.deliveryservice.order.domain.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_order_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct {

	@GeneratedValue
	@Id
	UUID id;

	@OneToOne
	Order order;

	Long productPrice;

	String productTitle;

	UUID productId;

	Long productQuantity;

	@Builder
	public OrderProduct(Order order, Long productPrice, String productTitle, UUID productId, Long productQuantity){
		this.order = order;
		this.productPrice = productPrice;
		this.productTitle = productTitle;
		this.productId = productId;
		this.productQuantity = productQuantity;
	}

}
