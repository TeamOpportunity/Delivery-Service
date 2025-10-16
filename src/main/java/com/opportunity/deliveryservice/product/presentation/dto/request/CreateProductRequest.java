package com.opportunity.deliveryservice.product.presentation.dto.request;

import com.opportunity.deliveryservice.product.domain.entity.ProductCategory;

import jakarta.annotation.Nullable;

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
	String aiPrompt
){

}