package com.opportunity.deliveryservice.cart.presentation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity.deliveryservice.cart.appilcation.service.CartServiceV1;
import com.opportunity.deliveryservice.cart.presentation.dto.request.ReqCartAddProductDto;
import com.opportunity.deliveryservice.cart.presentation.dto.response.ResCartGetByUserDto;
import com.opportunity.deliveryservice.cart.presentation.dto.response.ResCartProductsDto;
import com.opportunity.deliveryservice.global.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/cart")
public class CartControllerV1 {
	private final CartServiceV1 cartServiceV1;

	// 장바구니 조회
	@GetMapping
	public ApiResponse<ResCartGetByUserDto> getCart() {
		// 임시 userId 사용
		Long tempUserId = 1L;
		ResCartGetByUserDto cart = cartServiceV1.getCartByUser(tempUserId);
		return ApiResponse.success(cart);
	}

	// 장바구니에 상품 추가
	@PostMapping("/products")
	public ApiResponse<ResCartProductsDto> addProductToCart(@RequestBody ReqCartAddProductDto product) {
		// 임시 userId 사용
		Long tempUserId = 1L;
		ResCartProductsDto addedProduct = cartServiceV1.addProductToCart(tempUserId, product);
		return ApiResponse.success(addedProduct);
	}
}
