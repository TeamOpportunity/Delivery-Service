package com.opportunity.deliveryservice.order.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.opportunity.deliveryservice.order.domain.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
	// 주문 조회
	List<Order> findAllByUserIdOrderByCreatedAtDesc(Long userId);
	List<Order> findAllByOrderByCreatedAtDesc();
	Optional<Order> findById(UUID id);
	Optional<Order> findByIdAndUserId(UUID orderId, Long userId);
}
