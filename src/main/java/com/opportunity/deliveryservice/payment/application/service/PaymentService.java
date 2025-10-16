package com.opportunity.deliveryservice.payment.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
import com.opportunity.deliveryservice.payment.domain.entity.PaymentStatus;
import com.opportunity.deliveryservice.payment.domain.repository.PaymentRepository;
import com.opportunity.deliveryservice.payment.infrastructure.TossPaymentsClient;
import com.opportunity.deliveryservice.payment.infrastructure.dto.TossConfirmRequest;
import com.opportunity.deliveryservice.payment.infrastructure.dto.TossConfirmResponse;
import com.opportunity.deliveryservice.payment.presentation.dto.request.ConfirmPaymentRequest;
import com.opportunity.deliveryservice.payment.presentation.dto.request.IntentPaymentRequest;
import com.opportunity.deliveryservice.payment.presentation.dto.response.PaymentResponse;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;
	private final TossPaymentsClient tossPaymentsClient;

	/**
	 * 결제 대기
	 * @param orderId
	 */
	@Transactional
	public void intentPayment(UUID orderId, IntentPaymentRequest request) {
		Payment newPayment = Payment.builder()
			.order(getOrder(orderId))
			.amount(request.amount())
			.build();

		paymentRepository.save(newPayment);
	}

	/**
	 * 결제 승인
	 * @param request
	 */
	@Transactional(noRollbackFor = OpptyException.class)
	public void confirmPayment(ConfirmPaymentRequest request) {
		Order order = getOrder(request.orderId());
		Payment payment = paymentRepository.findByOrder(order);

		persistPaymentInfo(payment, request);

		processConfirmInNewTx(payment.getId(), request);
	}

	@Transactional(readOnly = true)
	public PaymentResponse getPayment(UUID orderId) {
		Payment payment = paymentRepository.findByOrder(getOrder(orderId));

		TossConfirmResponse response = getPaymentInfo(payment.getTossPaymentId());
		return PaymentResponse.of(response, payment.getId());
	}

	private void persistPaymentInfo(Payment payment, ConfirmPaymentRequest request) {
		payment.setPaymentInfo(request.tossPaymentKey(), request.tossOrderId());
		paymentRepository.saveAndFlush(payment);
	}

	private TossConfirmRequest buildTossConfirmRequest(ConfirmPaymentRequest request) {
		return TossConfirmRequest.builder()
			.orderId(request.tossOrderId())
			.paymentKey(request.tossPaymentKey())
			.amount(request.amount())
			.build();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = OpptyException.class)
	public void processConfirmInNewTx(UUID paymentId, ConfirmPaymentRequest request) {
	    Payment payment = paymentRepository.findById(paymentId).orElseThrow(
	        () -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND)
	    );

	    TossConfirmRequest req = buildTossConfirmRequest(request);
	    try {
	        TossConfirmResponse res = confirm(req);
	        payment.setPaymentResult(res.getStatus(), res.getMethod(), res.getApprovedAt());
	        validateResponseOrThrow(res, request.amount());
	    } catch (OpptyException e) {
			if(payment.getStatus() != PaymentStatus.DONE) {
				payment.setPaymentError(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
			}
	        throw e;
	    }
	}

	private TossConfirmResponse confirm(TossConfirmRequest tossConfirmRequest) {
		try {
			return tossPaymentsClient.confirm(tossConfirmRequest);
		} catch (TossRemoteException e) {
			log.warn("[PAY] Toss confirm failed: status={}, code={}, msg={}", e.getStatus(), e.getCode(),
				e.getMessage());

			String clientMsg = e.getTossMessage() != null ? e.getTossMessage() : "결제 처리 중 오류가 발생했습니다.";
			String clientCode = e.getCode() != null ? e.getCode() : "TOSS_UNKNOWN_ERROR";

			throw new OpptyException(new DynamicErrorCode(e.getStatus(), clientCode, clientMsg));
		}
	}

	private TossConfirmResponse getPaymentInfo(String tossPaymentId) {
		try {
			return tossPaymentsClient.getPaymentInfo(tossPaymentId);
		} catch (TossRemoteException e) {
			log.warn("[PAY] Toss getPaymentInfo failed: status={}, code={}, msg={}", e.getStatus(), e.getCode(),
				e.getMessage());

			String clientMsg = e.getTossMessage() != null ? e.getTossMessage() : "결제 조회 중 오류가 발생했습니다.";
			String clientCode = e.getCode() != null ? e.getCode() : "TOSS_UNKNOWN_ERROR";

			throw new OpptyException(new DynamicErrorCode(e.getStatus(), clientCode, clientMsg));
		}
	}

	private void validateResponseOrThrow(TossConfirmResponse res, Integer amount) {
		if (!PaymentStatus.DONE.toString().equalsIgnoreCase(res.getStatus())) {
			throw new OpptyException(ServerErrorCode.INVALID_PAYMENT_STATUS);
		}
		if (res.getTotalAmount() != null && !res.getTotalAmount().equals(amount)) {
			throw new OpptyException(ClientErrorCode.INVALID_PAYMENT_AMOUNT);
		}
	}

	private Order getOrder(UUID orderId) {
		return orderRepository.findById(orderId).orElseThrow(
			() -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND)
		);
	}

}
