package com.opportunity.deliveryservice.review.domain.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.opportunity.deliveryservice.review.domain.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
	@EntityGraph(attributePaths = "reply")//n+1문제 해결
	Page<Review> findByStoreIdAndDeletedAtIsNull(Long storeId, Pageable pageable);

	Page<Review> findByUserIdAndDeletedAtIsNull(Long targetUserId, Pageable pageable);

}
