package com.opportunity.deliveryservice.product.presentation.dto.response;

import java.util.UUID;

import com.opportunity.deliveryservice.product.domain.entity.Product;
import com.opportunity.deliveryservice.product.domain.entity.ProductCategory;

public record GetProductResponse(
	UUID productId,
	String title,
	Long price,
	String description,
	ProductCategory category,
	String image
) {
	public static GetProductResponse of(Product product){
		return new GetProductResponse(product.getId(), product.getTitle(), product.getPrice(), product.getDescription(),
			product.getCategory(),
			product.getImage());
	}
}
