package com.opportunity.deliveryservice.cart.domain.repositoy;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.opportunity.deliveryservice.cart.domain.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, UUID> {
	@EntityGraph(attributePaths = {"cartProducts", "cartProducts.product"})
	Optional<Cart> findByUserId(Long userId);
}
