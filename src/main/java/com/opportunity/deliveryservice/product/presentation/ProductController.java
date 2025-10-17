package com.opportunity.deliveryservice.product.presentation;

import java.util.UUID;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.opportunity.deliveryservice.global.infrastructure.config.security.UserDetailsImpl;
import com.opportunity.deliveryservice.product.application.service.ProductService;
import com.opportunity.deliveryservice.product.presentation.dto.request.CreateProductRequest;
import com.opportunity.deliveryservice.product.presentation.dto.request.UpdateProductRequest;
import com.opportunity.deliveryservice.product.presentation.dto.response.GetProductResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
public class ProductController {
	private final ProductService productService;


	@PostMapping
	@Secured("ROLE_OWNER")
	public ApiResponse<?> createProduct(
		@Valid @RequestBody CreateProductRequest request,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		productService.createProduct(request, userDetails.getUser());
		return ApiResponse.noContent();
	}

	@PutMapping("/{productId}")
	@Secured("ROLE_OWNER")
	public ApiResponse<?> updateProduct(
		@PathVariable UUID productId,
		@RequestBody UpdateProductRequest request,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	){
		productService.updateProduct(request, productId, userDetails.getUser());
		return ApiResponse.noContent();
	}

	@PatchMapping("/{productId}")
	@Secured("ROLE_OWNER")
	public ApiResponse<?> deleteProduct(
		@PathVariable UUID productId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	){
		productService.deleteProduct(productId, userDetails.getUser());
		return ApiResponse.noContent();
	}


	@GetMapping("/{productId}")
	public ApiResponse<GetProductResponse> getProductDetail(
		@PathVariable UUID productId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	){
		GetProductResponse response = GetProductResponse.of(productService.getProductDetail(productId));
		return ApiResponse.success(response);
	}

	@PatchMapping("/{productId}/visibility")
	@Secured("ROLE_OWNER")
	public ApiResponse<?> updateProductVisibility(
		@PathVariable UUID productId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	){
		productService.updateProductVisibility(productId, userDetails.getUser());
		return ApiResponse.noContent();
	}

}
