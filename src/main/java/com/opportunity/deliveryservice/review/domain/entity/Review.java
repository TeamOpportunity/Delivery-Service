package com.opportunity.deliveryservice.review.domain.entity;

import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.entity.BaseEntity;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.order.domain.entity.Order;
import com.opportunity.deliveryservice.store.domain.entity.Store;
import com.opportunity.deliveryservice.user.domain.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

	@Id
	@UuidGenerator
	private UUID id;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private int rating;

	@Column(nullable = true, columnDefinition = "TEXT")
	private String image;

	@Column(nullable = false)
	private boolean isVisible = true;

	@OneToOne(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private Reply reply;

	//여러 리뷰가 한 가게를 참조
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@JoinColumn(name = "user_id" , nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	//한개의 주문당 한개의 리뷰


	@Builder
	public Review(String content, int rating, String image, User user) {
		this.content = content;
		this.rating = rating;
		this.image = image;
		this.user = user;
		//userId
	}
	@OneToOne
	@JoinColumn(name = "order_id", nullable = false, unique = true)
	private Order order;

	public void updateReview(String content, String image) {
		Optional.ofNullable(content).ifPresent(value -> this.content = value);
		Optional.ofNullable(image).ifPresent(value -> this.image = value);
	}

	public void setOrder(Order order) {
		this.order = order;
		if (order.getReview() != this) {
			order.setReview(this);
		}
	}

	public void setStore(Store store) {
		this.store = store;
		if (!store.getReviews().contains(this)) {
			store.getReviews().add(this);
		}
	}

	public void setUser(User user) {
		this.user = user;
		if (!user.getReviews().contains(this)) {
			user.getReviews().add(this);
		}
	}

	public void setRating(int rating) {
		if (rating < 1 || rating > 5) {
			throw new OpptyException(ClientErrorCode.INVALID_RATING);
		}
		this.rating = rating;
	}

	public void setReply(Reply reply) {
		this.reply = reply;
		if (reply != null) {
			reply.setReview(this);
		}
	}

	public void softDelete(Long userId) {
		super.softDelete(userId); // BaseEntity에서 isDeleted, deletedBy 등 처리
		if (this.reply != null) {
			this.reply.softDelete(userId); // Reply도 soft delete
		}
	}

}
