package com.opportunity.deliveryservice.review.presentation.dto.response;

import com.opportunity.deliveryservice.review.domain.entity.Review;

public record GetReviewResponse(
	Long storeId,
	String content,
	int rating,
	String image,
	GetReplyResponse reply
) {
	public static GetReviewResponse of(Review review) {
		GetReplyResponse replyResponse = review.getReply() != null
			? GetReplyResponse.of(review.getReply())
			: null;
		return new GetReviewResponse(review.getStoreId(), review.getContent(), review.getRating(), review.getImage(),
			replyResponse);
	}
}
