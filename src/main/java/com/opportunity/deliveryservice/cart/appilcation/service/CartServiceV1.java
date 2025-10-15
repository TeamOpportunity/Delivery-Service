package com.opportunity.deliveryservice.cart.appilcation.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity.deliveryservice.cart.domain.entity.Cart;
import com.opportunity.deliveryservice.cart.domain.entity.CartProducts;
import com.opportunity.deliveryservice.cart.domain.repositoy.CartProductsRepository;
import com.opportunity.deliveryservice.cart.domain.repositoy.CartRepository;
import com.opportunity.deliveryservice.cart.presentation.dto.request.ReqCartAddProductDto;
import com.opportunity.deliveryservice.cart.presentation.dto.request.ReqCartUpdateQuantityDto;
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

	// 장바구니에 담긴 상품 수량 변경
	@Transactional
	public ResCartProductsDto updateProductQuantity(Long userId, UUID productId, ReqCartUpdateQuantityDto request) {
		// 유저 cart 조회 (없으면 생성)
		Cart cart = findCart(userId);

		// 유저 장바구니에서 상품 찾기
		CartProducts cartProducts = cartProductsRepository.findByCartIdAndProductId(cart.getId(), productId)
			.orElseThrow(() -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND));

		// 수량이 1보다 적으면 throw
		if (request.getQuantity() < 1) {
			throw new OpptyException(ClientErrorCode.INVALID_INPUT_VALUE);
		}

		// 수량이 1 이상이면 업데이트
		cartProducts.updateQuantity(request.getQuantity());

		return ResCartProductsDto.fromEntity(cartProducts);
	}


	private Cart findCart(Long userId) {
		return cartRepository.findByUserId(userId)
			.orElseGet(() -> cartRepository.save(new Cart(userId))); // cart가 없으면 생성
	}
}
