package com.opportunity.deliveryservice.review.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateReplyRequest(
	@NotBlank String content
) {

}
