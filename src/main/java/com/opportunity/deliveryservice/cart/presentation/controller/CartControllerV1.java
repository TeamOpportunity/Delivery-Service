package com.opportunity.deliveryservice.cart.presentation.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity.deliveryservice.cart.appilcation.service.CartServiceV1;
import com.opportunity.deliveryservice.cart.presentation.dto.request.ReqCartAddProductDto;
import com.opportunity.deliveryservice.cart.presentation.dto.request.ReqCartUpdateQuantityDto;
import com.opportunity.deliveryservice.cart.presentation.dto.response.ResCartGetByUserDto;
import com.opportunity.deliveryservice.cart.presentation.dto.response.ResCartProductsDto;
import com.opportunity.deliveryservice.global.common.response.ApiResponse;
import com.opportunity.deliveryservice.global.infrastructure.config.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/cart")
@Tag(name = "Cart", description = "장바구니 API")
public class CartControllerV1 {
	private final CartServiceV1 cartServiceV1;

	// 장바구니 조회
	@GetMapping
	public ApiResponse<ResCartGetByUserDto> getCart(@AuthenticationPrincipal UserDetailsImpl user) {
		ResCartGetByUserDto cart = cartServiceV1.getCartByUser(user.getUser().getId());
		return ApiResponse.success(cart);
	}

	// 장바구니에 상품 추가
	@PostMapping("/products")
	public ApiResponse<ResCartProductsDto> addProductToCart(
		@AuthenticationPrincipal UserDetailsImpl user,
		@RequestBody ReqCartAddProductDto product
	) {
		ResCartProductsDto addedProduct = cartServiceV1.addProductToCart(user.getUser().getId(), product);
		return ApiResponse.success(addedProduct);
	}

	// 장바구니에 담긴 상품 수량 변경
	@PatchMapping("/products/{productId}")
	public ApiResponse<ResCartProductsDto> updateProductQuantity(
		@AuthenticationPrincipal UserDetailsImpl user,
		@PathVariable UUID productId,
		@RequestBody ReqCartUpdateQuantityDto request
	) {
		ResCartProductsDto updatedProduct = cartServiceV1
			.updateProductQuantity(user.getUser().getId(), productId, request);
		return ApiResponse.success(updatedProduct);
	}

	// 장바구니 상품 삭제
	@DeleteMapping("/products/{productId}")
	public ApiResponse<Void> deleteProductFromCart(
		@AuthenticationPrincipal UserDetailsImpl user,
		@PathVariable UUID productId
	) {
		cartServiceV1.deleteProductFromCart(user.getUser().getId(), productId);
		return ApiResponse.noContent();
	}
}
