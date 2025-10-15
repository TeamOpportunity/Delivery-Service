package com.opportunity.deliveryservice.review.domain.entity;

import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.opportunity.deliveryservice.global.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(name = "p_reply")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply extends BaseEntity{

	@Id
	@UuidGenerator
	private UUID id;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private boolean isVisible = true; // 기본값 true


	@Setter
	@OneToOne
	@JoinColumn(name = "review_id")
	private Review review;

	@Builder
	public Reply(String content, Review review) {
		this.review = review;
		this.content = content;
	}

	public void updateReply(String content) {
		Optional.ofNullable(content).ifPresent(value -> this.content = value);
	}
}
