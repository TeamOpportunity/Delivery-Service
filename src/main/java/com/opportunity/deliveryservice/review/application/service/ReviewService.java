package com.opportunity.deliveryservice.review.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.review.domain.entity.Review;
import com.opportunity.deliveryservice.review.domain.exception.ReviewErrorCode;
import com.opportunity.deliveryservice.review.domain.repository.ReviewRepository;
import com.opportunity.deliveryservice.review.presentation.dto.request.CreateReviewRequest;
import com.opportunity.deliveryservice.review.presentation.dto.request.UpdateReviewRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewRepository reviewRepository;

	@Transactional
	public void createReview(CreateReviewRequest request, Long storeId){
		validate();

		Review newReview = Review.builder()
			.content(request.content())
			.image(request.image())
			.storeId(storeId)
			.build();

		newReview.setRating(request.rating());
		reviewRepository.save(newReview);
	}

	@Transactional
	public void updateReview(UpdateReviewRequest request, UUID reviewId){
		validate();
		Review updateReview = getReviewId(reviewId);
		updateReview.updateReview(request.content(), request.image());
		if(request.rating() != null){
			updateReview.setRating(request.rating());
		}
		reviewRepository.save(updateReview);
	}

	@Transactional
	public void deleteReview(UUID reviewId, Long userId){
		validate();
		Review deletedReview = getReviewId(reviewId);
		deletedReview.softDelete(userId);
		// 실제 삭제 로직은 Soft Delete 처리 가능
	}

	@Transactional(readOnly = true)
	public List<Review> getReviews(Long storeId, Pageable pageable){
		validate();
		Page<Review> reviewPage = reviewRepository.findByStoreIdAndDeletedAtIsNull(storeId, pageable);
		return findReviewsOrThrow(reviewPage);
	}

    /*
    @Transactional(readOnly = true)
    public List<Review> getUserReviews(Long targetUserId, Long loginUserId, String role, Pageable pageable){
        validate();
        // 관리자만 특정 유저 리뷰 조회 가능
        if (!targetUserId.equals(loginUserId)) {
            if (!role.equals("MANAGER") && !role.equals("MASTER")) {
                throw new OpptyException(ReviewErrorCode.FORBIDDEN);
            }
        }
        Page<Review> reviewPage = reviewRepository.findByUserIdAndDeletedAtIsNull(targetUserId, pageable);
        return findReviewsOrThrow(reviewPage);
    }
    */

	@Transactional(readOnly = true)
	public Review getReviewsDetail(UUID reviewId){
		validate();
		return getReviewId(reviewId);
	}

	private void validate(){
		// 사용자 검증 코드 구현 (추후)
	}

	private Review getReviewId(UUID reviewId){
		return reviewRepository.findById(reviewId).orElseThrow(
			() -> new OpptyException(ReviewErrorCode.REVIEW_NOT_FOUND)
		);
	}

	private List<Review> findReviewsOrThrow(Page<Review> reviewPage) {
		if (reviewPage.isEmpty()) {
			throw new OpptyException(ReviewErrorCode.REVIEW_NOT_FOUND);
		}
		return reviewPage.getContent();
	}
}
