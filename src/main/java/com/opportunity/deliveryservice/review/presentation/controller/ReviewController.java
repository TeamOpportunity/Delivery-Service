package com.opportunity.deliveryservice.review.presentation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import com.opportunity.deliveryservice.global.common.response.ApiResponse;
import com.opportunity.deliveryservice.review.application.service.ReviewService;
import com.opportunity.deliveryservice.review.presentation.dto.request.CreateReviewRequest;
import com.opportunity.deliveryservice.review.presentation.dto.request.UpdateReviewRequest;
import com.opportunity.deliveryservice.review.presentation.dto.response.GetReviewResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class ReviewController {

	private final ReviewService reviewService;

	private static final int[] ALLOWED_PAGE_SIZES = {10, 30, 50};

	@PostMapping("/stores/reviews")
	public ApiResponse<?> createReview(
		@RequestBody CreateReviewRequest request,
		@RequestParam Long storeId
			// @AuthenticationPrincipal UserPrincipal principal
	){
		reviewService.createReview(request, storeId);
		return ApiResponse.noContent();
	}


	@PutMapping("/reviews/{reviewId}")
	public ApiResponse<?> updateReview(
		@PathVariable UUID reviewId,
		@RequestBody UpdateReviewRequest request
		// @AuthenticationPrincipal UserPrincipal principal
	){
		reviewService.updateReview(request, reviewId);
		return ApiResponse.noContent();
	}


	@PatchMapping("/reviews/{reviewId}")
	public ApiResponse<?> deleteReview(
		@PathVariable UUID reviewId,
		@RequestParam Long userId
		//  @AuthenticationPrincipal UserPrincipal principal
		// Long userId = principal.getId();
	){
		reviewService.deleteReview(reviewId, userId);
		return ApiResponse.noContent();
	}

	// @GetMapping("/users/me/reviews")
	// public ApiResponse<List<GetReviewResponse>> getMyReviews(
	// 	@RequestParam(defaultValue = "0") int page,
	// 	@RequestParam(defaultValue = "10") int size,
	// 	@RequestParam(defaultValue = "createdAt,desc") String sort
	// 	// @AuthenticationPrincipal UserPrincipal principal
	// ){
	// 	size = sanitizePageSize(size);
	// 	PageRequest pageRequest = buildPageRequest(page, size, sort);
	//
	// 	List<GetReviewResponse> responses = reviewService.getUserReviews(/*principal.getId()*/ 1L,
	// 			// /*principal.getId()*/ 1L,
	// 			// /*isAdmin*/ false,
	// 			pageRequest).stream()
	// 		.map(GetReviewResponse::of)
	// 		.toList();
	//
	// 	return ApiResponse.success(responses);
	// }

	// @GetMapping("/users/{userId}/reviews")
	// public ApiResponse<List<GetReviewResponse>> getReviewsByUser(
	// 	@PathVariable Long userId,
	// 	@RequestParam(defaultValue = "0") int page,
	// 	@RequestParam(defaultValue = "10") int size,
	// 	@RequestParam(defaultValue = "createdAt,desc") String sort
	// 	// @AuthenticationPrincipal UserPrincipal principal
	// ){
	// 	size = sanitizePageSize(size);
	// 	PageRequest pageRequest = buildPageRequest(page, size, sort);
	//
	// 	List<GetReviewResponse> responses = reviewService.getUserReviews(userId,
	// 			// /*loginUserId*/ 1L,
	// 			// /*isAdmin*/ true,
	// 			pageRequest).stream()
	// 		.map(GetReviewResponse::of)
	// 		.toList();
	//
	// 	return ApiResponse.success(responses);
	// }

	@GetMapping("/stores/{storeId}/reviews")
	public ApiResponse<List<GetReviewResponse>> getStoreReviews(
		@PathVariable Long storeId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "createdAt,desc") String sort
		// @AuthenticationPrincipal UserPrincipal principal
	){
		size = sanitizePageSize(size);
		PageRequest pageRequest = buildPageRequest(page, size, sort);

		List<GetReviewResponse> responses = reviewService.getReviews(storeId, pageRequest).stream()
			.map(GetReviewResponse::of)
			.toList();

		return ApiResponse.success(responses);
	}

	// ------------------- private -------------------
	private PageRequest buildPageRequest(int page, int size, String sort) {
		String[] sortParams = sort.split(",");
		return PageRequest.of(page, size,
			Sort.by(Sort.Order.by(sortParams[0]).with(Sort.Direction.fromString(sortParams[1]))));
	}

	private int sanitizePageSize(int size){
		for (int allowed : ALLOWED_PAGE_SIZES){
			if (size == allowed) return size;
		}
		return 10; // 기본값
	}
}
