package com.opportunity.deliveryservice.review.presentation.dto.response;

import com.opportunity.deliveryservice.review.domain.entity.Reply;

public record GetReplyResponse(
	String reviewId,   // 답글이 연결된 리뷰 ID
	String content
) {
	public static GetReplyResponse of(Reply reply) {
		return new GetReplyResponse(
			reply.getReview() != null ? reply.getReview().getId().toString() : null,
			reply.getContent()
		);
	}
}
