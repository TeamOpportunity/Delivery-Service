package com.opportunity.deliveryservice.order.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.opportunity.deliveryservice.order.domain.entity.Order;
import com.opportunity.deliveryservice.order.domain.entity.OrderProduct;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, UUID> {
	// 주문 조회
	Optional<OrderProduct> findByOrder(Order order);
	List<OrderProduct> findAllByOrder_In(Collection<UUID> orderIds);
}
