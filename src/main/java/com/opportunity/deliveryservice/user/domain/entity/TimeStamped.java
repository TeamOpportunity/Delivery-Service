package com.opportunity.deliveryservice.user.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

// isDeleted가 true일 경우 'deleted_at IS NULL' 조건을 무시함
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class TimeStamped {

	@CreatedDate
	@Column(name = "created_At", updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "modified_At")
	private LocalDateTime modifiedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt; // Soft Delete 필드
}
