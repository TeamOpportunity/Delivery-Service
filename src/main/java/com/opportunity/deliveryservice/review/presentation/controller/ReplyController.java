package com.opportunity.deliveryservice.review.presentation.controller;

import java.security.Principal;
import java.util.UUID;

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
import com.opportunity.deliveryservice.review.application.service.ReplyService;
import com.opportunity.deliveryservice.review.presentation.dto.request.CreateReplyRequest;
import com.opportunity.deliveryservice.review.presentation.dto.request.UpdateReplyRequest;
import com.opportunity.deliveryservice.review.presentation.dto.response.GetReplyResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reviews/{reviewId}/reply")
@Tag(name = "Review", description = "리뷰 API")
public class ReplyController {

	private final ReplyService replyService;

	@PostMapping
	public ApiResponse<?> createReply(
		@RequestBody CreateReplyRequest request,
		@PathVariable UUID reviewId,
		@AuthenticationPrincipal UserDetailsImpl principal
	) {
		replyService.createReply(request, reviewId, principal.getUser());
		return ApiResponse.noContent();
	}

	@PutMapping
	public ApiResponse<?> updateReply(
		@PathVariable UUID reviewId,
		@RequestBody UpdateReplyRequest request,
		@AuthenticationPrincipal UserDetailsImpl principal
	) {
		replyService.updateReply(reviewId, request, principal.getUser());
		return ApiResponse.noContent();
	}

	@PatchMapping
	public ApiResponse<?> deleteReply(
		@PathVariable UUID reviewId,
		@RequestParam Long userId,
		@AuthenticationPrincipal UserDetailsImpl principal
	) {
		replyService.deleteReply(reviewId, principal.getUser());
		return ApiResponse.noContent();
	}

	@GetMapping
	public ApiResponse<GetReplyResponse> getReply(
		@PathVariable UUID reviewId
	) {
		GetReplyResponse replyResponse = GetReplyResponse.of(replyService.getReply(reviewId));
		return ApiResponse.success(replyResponse);
	}
}
