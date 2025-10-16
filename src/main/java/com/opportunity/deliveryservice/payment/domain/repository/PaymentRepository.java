package com.opportunity.deliveryservice.payment.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opportunity.deliveryservice.order.domain.entity.Order;
import com.opportunity.deliveryservice.payment.domain.entity.Payment;
import com.opportunity.deliveryservice.payment.domain.entity.PaymentStatus;
import com.opportunity.deliveryservice.payment.presentation.PaymentController;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

	Payment findByOrder(Order order);
}
