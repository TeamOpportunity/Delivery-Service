package com.opportunity.deliveryservice.cart.appilcation.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity.deliveryservice.cart.domain.entity.Cart;
import com.opportunity.deliveryservice.cart.domain.entity.CartProducts;
import com.opportunity.deliveryservice.cart.domain.repositoy.CartProductsRepository;
import com.opportunity.deliveryservice.cart.domain.repositoy.CartRepository;
import com.opportunity.deliveryservice.cart.presentation.dto.request.ReqCartAddProductDto;
import com.opportunity.deliveryservice.cart.presentation.dto.response.ResCartGetByUserDto;
import com.opportunity.deliveryservice.cart.presentation.dto.response.ResCartProductsDto;
import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.product.domain.entity.Product;
import com.opportunity.deliveryservice.product.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceV1 {
	private final CartRepository cartRepository;
	private final CartProductsRepository cartProductsRepository;
	private final ProductRepository productRepository;

	// 장바구니 조회
	@Transactional
	public ResCartGetByUserDto getCartByUser(Long userId) {
		Cart cart = findCart(userId);

		return ResCartGetByUserDto.fromEntity(cart, cart.getCartProducts().stream()
			.map(ResCartProductsDto::fromEntity)
			.toList());
	}

	// 장바구니에 상품 추가
	@Transactional
	public ResCartProductsDto addProductToCart(Long userId, ReqCartAddProductDto request) {
		// 유저 cart 조회 (없으면 생성)
		Cart cart = findCart(userId);

		// 존재하는 상품인지 확인(유효 상품인지 확인)
		Product product = productRepository.findById(request.getProductId())
			.orElseThrow(() -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND));

		// 중복 상품 존재 여부 확인
		Optional<CartProducts> foundProduct = cartProductsRepository
			.findByCartIdAndProductId(cart.getId(), request.getProductId());

		CartProducts cartProduct;

		if (foundProduct.isPresent()) {
			// 이미 담겨있으면 수량 추가
			cartProduct = foundProduct.get();
			cartProduct.updateQuantity(cartProduct.getQuantity() + request.getQuantity());
		} else {
			// 새 상품이면 추가
			cartProduct = CartProducts.builder()
				.cart(cart)
				.product(product)
				.quantity(request.getQuantity())
				.build();
			cartProductsRepository.save(cartProduct);
		}

		return ResCartProductsDto.fromEntity(cartProduct);
	}

	private Cart findCart(Long userId) {
		return cartRepository.findByUserId(userId)
			.orElseGet(() -> cartRepository.save(new Cart(userId))); // cart가 없으면 생성
	}
}
