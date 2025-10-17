package com.opportunity.deliveryservice.product.presentation.dto.request;

import com.opportunity.deliveryservice.product.domain.entity.ProductCategory;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateProductRequest (
	UUID storeId,
	String title,
	Long price,
	@Nullable
	String description,
	ProductCategory category,
	String image,
	Boolean useAI,

	@Nullable
	@Size(max = 50, message = "AI 프롬프트는 50자 이내로 작성해주세요.")
	String aiPrompt
){
}