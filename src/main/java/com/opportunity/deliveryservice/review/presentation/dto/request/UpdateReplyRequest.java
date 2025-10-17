package com.opportunity.deliveryservice.review.presentation.dto.request;

import javax.annotation.Nullable;

public record UpdateReplyRequest(
	@Nullable String content
) {
}
