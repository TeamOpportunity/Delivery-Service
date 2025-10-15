package com.opportunity.deliveryservice.review.domain.entity;

import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.opportunity.deliveryservice.global.common.entity.BaseEntity;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.review.domain.exception.ReviewErrorCode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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
public class Review extends BaseEntity{

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

	@Column(nullable = false) //임시 스토어 아이디
	private Long storeId;

	//User Many To One
	//Order One To One

	@Builder
	public Review(String content, int rating, String image,Long storeId) {
		this.content = content;
		this.rating = rating;
		this.image = image;
		this.storeId = storeId;
		//userId
	}

	public void updateReview(String content, String image) {
		Optional.ofNullable(content).ifPresent(value -> this.content = value);
		Optional.ofNullable(image).ifPresent(value -> this.image = value);
	}

	public void setRating(int rating) {
		if (rating < 1 || rating > 5) {
			throw new OpptyException(ReviewErrorCode.INVALID_RATING);
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
