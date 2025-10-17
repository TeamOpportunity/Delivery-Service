package com.opportunity.deliveryservice.review.presentation.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReviewRequest(
	@NotNull UUID orderId,
	@NotBlank String content,
	@Min(1) @Max(5) int rating,
	String image
) {

}