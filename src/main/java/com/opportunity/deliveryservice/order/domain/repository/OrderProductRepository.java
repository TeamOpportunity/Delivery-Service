package com.opportunity.deliveryservice.order.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opportunity.deliveryservice.order.domain.entity.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, UUID> {
}
