package com.opportunity.deliveryservice.cart.domain.repositoy;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opportunity.deliveryservice.cart.domain.entity.CartProducts;

public interface CartProductsRepository extends JpaRepository<CartProducts, UUID> {
	Optional<CartProducts> findByCartIdAndProductId(UUID cartId, UUID productId);
}
