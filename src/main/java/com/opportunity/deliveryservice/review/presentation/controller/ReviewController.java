package com.opportunity.deliveryservice.review.presentation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity.deliveryservice.global.common.response.ApiResponse;
import com.opportunity.deliveryservice.global.infrastructure.config.security.UserDetailsImpl;
import com.opportunity.deliveryservice.review.application.service.ReviewService;
import com.opportunity.deliveryservice.review.domain.entity.Review;
import com.opportunity.deliveryservice.review.presentation.dto.request.CreateReviewRequest;
import com.opportunity.deliveryservice.review.presentation.dto.request.UpdateReviewRequest;
import com.opportunity.deliveryservice.review.presentation.dto.response.GetReviewResponse;
import com.opportunity.deliveryservice.user.domain.entity.User;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Tag(name = "Review", description = "리뷰 API")
public class ReviewController {

	private final ReviewService reviewService;

	private static final int[] ALLOWED_PAGE_SIZES = {10, 30, 50};

	//리뷰 생성
	@PostMapping("/stores/{storeId}/reviews")
	public ApiResponse<?> createReview(
		@PathVariable Long storeId,
		@Valid @RequestBody CreateReviewRequest request,
		@AuthenticationPrincipal UserDetailsImpl principal
	){
		User user = principal.getUser();
		reviewService.createReview(request, storeId, user);
		return ApiResponse.noContent();
	}

	//리뷰 업데이트
	@PutMapping("/reviews/{reviewId}")
	public ApiResponse<?> updateReview(
		@PathVariable UUID reviewId,
		@RequestBody UpdateReviewRequest request,
		@AuthenticationPrincipal UserDetailsImpl principal
	){
		reviewService.updateReview(request, reviewId, principal.getUser());
		return ApiResponse.noContent();
	}

	//리뷰 삭제
	@PatchMapping("/reviews/{reviewId}")
	public ApiResponse<?> deleteReview(
		@PathVariable UUID reviewId,
		@AuthenticationPrincipal UserDetailsImpl principal
		// Long userId = principal.getId();
	){
		reviewService.deleteReview(reviewId, principal.getUser());
		return ApiResponse.noContent();
	}

	//특정 가게의 리뷰 페이지
	//권한이 필요 없으므로 SecurityConfig에서 인증 제외 경로 등록
	@GetMapping("/stores/{storeId}/reviews")
	public ApiResponse<List<GetReviewResponse>> getStoreReviews(
		@PathVariable Long storeId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "createdAt,desc") String sort
	){
		size = sanitizePageSize(size);
		PageRequest pageRequest = buildPageRequest(page, size, sort);

		List<GetReviewResponse> responses = reviewService.getStoreReviews(storeId, pageRequest).stream()
			.map(GetReviewResponse::of)
			.toList();

		return ApiResponse.success(responses);
	}

	//특정 리뷰 상세 페이지
	//권한이 필요 없으므로 SecurityConfig에서 인증 제외 경로 등록
	@GetMapping("/reviews/{reviewId}")
	public ApiResponse<GetReviewResponse> getReviewDetail(
		@PathVariable UUID reviewId
	){
		Review getReviewDetail = reviewService.getReviewDetail(reviewId);
		return ApiResponse.success(GetReviewResponse.of(getReviewDetail));
	}
	//유저용 내 리뷰 페이지
	@GetMapping("/users/me/reviews")
	public ApiResponse<List<GetReviewResponse>> getMyReviews(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "createdAt,desc") String sort,
		@AuthenticationPrincipal UserDetailsImpl principal
	){
		size = sanitizePageSize(size);
		PageRequest pageRequest = buildPageRequest(page, size, sort);

		List<GetReviewResponse> responses = reviewService.getMyReviews(principal.getUser(), pageRequest)
			.stream()
			.map(GetReviewResponse::of)
			.toList();

		return ApiResponse.success(responses);
	}

	//관리자용 특정 유저 리뷰 페이지
	@GetMapping("/users/{userId}/reviews")
	public ApiResponse<List<GetReviewResponse>> getReviewsByUser(
		@PathVariable Long userId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "createdAt,desc") String sort,
		@AuthenticationPrincipal UserDetailsImpl principal
	){
		size = sanitizePageSize(size);
		PageRequest pageRequest = buildPageRequest(page, size, sort);

		List<GetReviewResponse> responses = reviewService.getUserReviewsForAdmin(userId, pageRequest)
			.stream()
			.map(GetReviewResponse::of)
			.toList();

		return ApiResponse.success(responses);
	}

	//pageRequest 객체 생성
	private PageRequest buildPageRequest(int page, int size, String sort) {
		String[] sortParams = sort.split(",");
		return PageRequest.of(page, size,
			Sort.by(Sort.Order.by(sortParams[0]).with(Sort.Direction.fromString(sortParams[1]))));
	}

	//페이지 사이즈 제한
	private int sanitizePageSize(int size){
		for (int allowed : ALLOWED_PAGE_SIZES){
			if (size == allowed) return size;
		}
		return 10; // 기본값
	}
}
