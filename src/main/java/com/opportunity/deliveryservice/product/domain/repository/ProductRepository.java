package com.opportunity.deliveryservice.product.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opportunity.deliveryservice.product.domain.entity.Product;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
