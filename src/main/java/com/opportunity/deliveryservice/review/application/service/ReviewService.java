package com.opportunity.deliveryservice.review.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.global.infrastructure.config.security.UserDetailsImpl;
import com.opportunity.deliveryservice.review.domain.entity.Review;
import com.opportunity.deliveryservice.review.domain.repository.ReviewRepository;
import com.opportunity.deliveryservice.review.presentation.dto.request.CreateReviewRequest;
import com.opportunity.deliveryservice.review.presentation.dto.request.UpdateReviewRequest;
import com.opportunity.deliveryservice.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewRepository reviewRepository;

	@Transactional
	public void createReview(CreateReviewRequest request, Long storeId, User user){

		//주문 했는지
		//주문을 하고 첫 리뷰인지.

		Review newReview = Review.builder()
			.content(request.content())
			.image(request.image())
			.storeId(storeId)
			.user(user)
			.build();
		newReview.setRating(request.rating());

		reviewRepository.save(newReview);
	}

	@Transactional
	public void updateReview(UpdateReviewRequest request, UUID reviewId, User currentUser){
		Review updateReview = getReviewId(reviewId);
		//본인이 맞는지 or 관리자인지
		validateAuthorOrAdmin(updateReview, currentUser);

		//리뷰 업데이트
		updateReview.updateReview(request.content(), request.image());
		if(request.rating() != null){
			updateReview.setRating(request.rating());
		}
	}

	@Transactional
	public void deleteReview(UUID reviewId, User currentUser){

		Review deleteReview = getReviewId(reviewId);
		//본인이 맞는지 or 관리자인지
		validateAuthorOrAdmin(deleteReview, currentUser);

		//softDelete 처리
		deleteReview.softDelete(currentUser.getId());

	}

	//가게 리뷰 조회
	@Transactional(readOnly = true)
	public List<Review> getStoreReviews(Long storeId, Pageable pageable){
		Page<Review> reviewPage = reviewRepository.findByStoreIdAndDeletedAtIsNull(storeId, pageable);
		return findReviews(reviewPage);
	}

	//내 리뷰 조회
	@Transactional(readOnly = true)
	public List<Review> getMyReviews(User currentUser, Pageable pageable){
		if(currentUser == null){
			throw new OpptyException(ClientErrorCode.UNAUTHORIZED);
		}
		Page<Review> getMyReviews = reviewRepository.findByUserIdAndDeletedAtIsNull(currentUser.getId(), pageable);
		return findReviews(getMyReviews);
	}

	//관리자용 유저 리뷰 조회
	@Transactional(readOnly = true)
	public List<Review> getUserReviewsForAdmin(Long targetUserId, Pageable pageable){
		validateAdmin();

		Page<Review> userReviewPage = reviewRepository.findByUserIdAndDeletedAtIsNull(targetUserId, pageable);

		return findReviews(userReviewPage);
	}

	//리뷰 상세 조회
	@Transactional(readOnly = true)
	public Review getReviewDetail(UUID reviewId) {
		Review getReviewDetail = getReviewId(reviewId);

		if(getReviewDetail.getDeletedAt() != null){
			throw new OpptyException(ClientErrorCode.REVIEW_NOT_FOUND);
		}

		return getReviewDetail;
	}

	//관리자인지
	private void validateAdmin() {
		User currentUser = getCurrentUser();
		String role = currentUser.getRole().toString();
		if (!"MANAGER".equals(role) && !"MASTER".equals(role)) {
			throw new OpptyException(ClientErrorCode.FORBIDDEN);
		}
	}
	//관리자 or 리뷰 작성자인지
	private void validateAuthorOrAdmin(Review review, User currentUser) {
		String role = currentUser.getRole().toString();
		if (!review.getUser().getId().equals(currentUser.getId())
			&& !"MANAGER".equals(role)
			&& !"MASTER".equals(role)) {
			throw new OpptyException(ClientErrorCode.FORBIDDEN);
		}
	}

	//로그인한 사용자 정보 가져오기
	private User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new OpptyException(ClientErrorCode.UNAUTHORIZED);
		}
		return ((UserDetailsImpl) authentication.getPrincipal()).getUser();
	}

	private Review getReviewId(UUID reviewId){
		return reviewRepository.findById(reviewId).orElseThrow(
			() -> new OpptyException(ClientErrorCode.REVIEW_NOT_FOUND)
		);
	}

	private List<Review> findReviews(Page<Review> reviewPage) {
		return reviewPage.hasContent() ? reviewPage.getContent() : List.of();
	}
}
