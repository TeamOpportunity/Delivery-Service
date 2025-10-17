package com.opportunity.deliveryservice.order.domain.entity;

import java.util.UUID;

import com.opportunity.deliveryservice.global.common.entity.BaseEntity;
import com.opportunity.deliveryservice.review.domain.entity.Review;
import com.opportunity.deliveryservice.payment.domain.entity.Payment;
import com.opportunity.deliveryservice.store.domain.entity.Store;
import com.opportunity.deliveryservice.user.domain.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

	@GeneratedValue
	@Id
	UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	User user;

	@Enumerated(value = EnumType.STRING)
	OrderProgress progress = OrderProgress.ORDER_REQUESTED;

	@Column(nullable = false)
	Integer amount;

	String request;

	// 주문과 리뷰 1:1
	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private Review review;
	@ManyToOne(fetch = FetchType.LAZY)
	Store store;

	@Builder
	public Order(Integer amount, String request, User user, Store store) {
		this.amount = amount;
		this.request = request;
		this.user = user;
		this.store = store;
	}

	// 편의 메서드
	public void setReview(Review review) {
		this.review = review;
		if (review.getOrder() != this) {
			review.setOrder(this);
		}
	}

	public void changeProgress(OrderProgress progress){
		this.progress = progress;
	}
}


