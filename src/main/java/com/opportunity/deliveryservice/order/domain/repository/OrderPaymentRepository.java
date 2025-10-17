package com.opportunity.deliveryservice.order.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.opportunity.deliveryservice.order.domain.entity.Order;
import com.opportunity.deliveryservice.payment.domain.entity.Payment;

@Repository
public interface OrderPaymentRepository extends JpaRepository<Payment, UUID> {
	Optional<Payment> findByOrder(Order order);
}
