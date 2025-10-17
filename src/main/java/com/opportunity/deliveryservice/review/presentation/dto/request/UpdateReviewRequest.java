package com.opportunity.deliveryservice.review.presentation.dto.request;

import javax.annotation.Nullable;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateReviewRequest(
	@Nullable String content,
	@Min(1) @Max(5) Integer rating,
	@Nullable String image) {

}
