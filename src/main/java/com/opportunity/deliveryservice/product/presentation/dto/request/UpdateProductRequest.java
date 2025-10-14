package com.opportunity.deliveryservice.product.presentation.dto.request;

import com.opportunity.deliveryservice.product.domain.entity.ProductCategory;

import jakarta.annotation.Nullable;

public record UpdateProductRequest  (
	@Nullable
	String title,
	@Nullable
	Long price,
	@Nullable
	String description,
	@Nullable
	ProductCategory category,
	@Nullable
	String image
){

}
