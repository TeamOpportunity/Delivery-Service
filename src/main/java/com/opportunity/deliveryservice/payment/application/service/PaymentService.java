package com.opportunity.deliveryservice.payment.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity.deliveryservice.global.common.code.BaseErrorCode;
import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.code.DynamicErrorCode;
import com.opportunity.deliveryservice.global.common.code.ServerErrorCode;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.global.common.exception.TossRemoteException;
import com.opportunity.deliveryservice.order.domain.entity.Order;
import com.opportunity.deliveryservice.order.domain.repository.OrderRepository;
import com.opportunity.deliveryservice.payment.domain.entity.Payment;
import com.opportunity.deliveryservice.payment.domain.entity.TossPaymentStatus;
import com.opportunity.deliveryservice.payment.domain.repository.PaymentRepository;
import com.opportunity.deliveryservice.payment.infrastructure.TossPaymentsClient;
import com.opportunity.deliveryservice.payment.infrastructure.dto.TossConfirmRequest;
import com.opportunity.deliveryservice.payment.infrastructure.dto.TossConfirmResponse;
import com.opportunity.deliveryservice.payment.presentation.dto.request.CreatePaymentRequest;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;
	private final TossPaymentsClient tossPaymentsClient;

	@Transactional
	public void confirmPayment(CreatePaymentRequest request) {
	    Order order = getOrder(request.orderId());

	    TossConfirmRequest tossConfirmRequest = TossConfirmRequest.builder()
	        .orderId(request.tossOrderId())
	        .paymentKey(request.tossPaymentKey())
	        .amount(request.amount())
	        .build();

		TossConfirmResponse res = confirm(tossConfirmRequest);
	    checkRes(res, request.amount());

	    Payment newPayment = Payment.builder()
	        .order(order)
	        .amount(request.amount())
	        .tossPaymentId(request.tossPaymentKey())
	        .tossOrderId(request.tossOrderId())
			.status(res.getStatus())
			.approvedAt(res.getApprovedAt())
			.method(res.getMethod())
	        .build();

	    paymentRepository.save(newPayment);
	}

	private TossConfirmResponse confirm(TossConfirmRequest tossConfirmRequest){
		try {
			return tossPaymentsClient.confirm(tossConfirmRequest);
		} catch (TossRemoteException e) {
			log.warn("[PAY] Toss confirm failed: status={}, code={}, msg={}", e.getStatus(), e.getCode(), e.getMessage());

			String clientMsg = e.getTossMessage() != null ? e.getTossMessage() : "결제 처리 중 오류가 발생했습니다.";
			String clientCode = e.getCode() != null ? e.getCode() : "TOSS_UNKNOWN_ERROR";

			throw new OpptyException(new DynamicErrorCode(e.getStatus(), clientCode, clientMsg));
		}
	}
	private void checkRes(TossConfirmResponse res, Integer amount){
		if (!TossPaymentStatus.DONE.toString().equalsIgnoreCase(res.getStatus())) {
			throw new OpptyException(ServerErrorCode.INVALID_PAYMENT_STATUS);
		}
		if (res.getTotalAmount() != null && !res.getTotalAmount().equals(amount)) {
			throw new OpptyException(ClientErrorCode.INVALID_PAYMENT_AMOUNT);
		}
	}

	private Order getOrder(UUID orderId){
		return orderRepository.findById(orderId).orElseThrow(
			() -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND)
		);
	}
}
