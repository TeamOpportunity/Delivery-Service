package com.opportunity.deliveryservice.product.presentation;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity.deliveryservice.global.common.response.ApiResponse;
import com.opportunity.deliveryservice.product.application.service.ProductService;
import com.opportunity.deliveryservice.product.presentation.dto.request.CreateProductRequest;
import com.opportunity.deliveryservice.product.presentation.dto.request.UpdateProductRequest;
import com.opportunity.deliveryservice.product.presentation.dto.response.GetProductResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
public class ProductController {
	private final ProductService productService;


	@PostMapping
	public ApiResponse<?> createProduct(
		@RequestBody CreateProductRequest request
		// @AuthenticationPrincipal
	) {
		productService.createProduct(request);
		return ApiResponse.noContent();
	}

	@PutMapping("/{productId}")
	public ApiResponse<?> updateProduct(
		@PathVariable UUID productId,
		@RequestBody UpdateProductRequest request
		// @AuthenticationPrincipal
	){
		productService.updateProduct(request, productId);
		return ApiResponse.noContent();
	}

	@PatchMapping("/{productId}")
	public ApiResponse<?> deleteProduct(
		@PathVariable UUID productId
		// @AuthenticationPrincipal
	){
		productService.deleteProduct(productId, 1L); //todo- userId 변경
		return ApiResponse.noContent();
	}


	@GetMapping("/{productId}")
	public ApiResponse<GetProductResponse> getProductDetail(
		@PathVariable UUID productId
		// @AuthenticationPrincipal
	){
		GetProductResponse response = GetProductResponse.of(productService.getProductDetail(productId));
		return ApiResponse.success(response);
	}

	@PatchMapping("/{productId}/visibility")
	public ApiResponse<?> updateProductVisibility(
		@PathVariable UUID productId
		// @AuthenticationPrincipal
	){
		productService.updateProductVisibility(productId);
		return ApiResponse.noContent();
	}

}
