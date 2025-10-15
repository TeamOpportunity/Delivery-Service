package com.opportunity.deliveryservice.cart.appilcation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity.deliveryservice.cart.domain.entity.Cart;
import com.opportunity.deliveryservice.cart.domain.repositoy.CartRepository;
import com.opportunity.deliveryservice.cart.presentation.dto.response.ResCartGetByUserDto;
import com.opportunity.deliveryservice.cart.presentation.dto.response.ResCartProductsDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceV1 {
	private final CartRepository cartRepository;

	// 장바구니 조회
	@Transactional
	public ResCartGetByUserDto getCartByUser(Long userId) {
		Cart cart = cartRepository.findByUserId(userId)
			.orElseGet(() -> cartRepository.save(new Cart(userId))); // cart가 없으면 생성

		return ResCartGetByUserDto.fromEntity(cart, cart.getCartProducts().stream()
			.map(ResCartProductsDto::fromEntity)
			.toList());
	}

}
