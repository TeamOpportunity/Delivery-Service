package com.opportunity.deliveryservice.payment.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.code.ServerErrorCode;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.order.domain.entity.Order;
import com.opportunity.deliveryservice.order.domain.repository.OrderRepository;
import com.opportunity.deliveryservice.payment.domain.entity.Payment;
import com.opportunity.deliveryservice.payment.domain.entity.PaymentStatus;
import com.opportunity.deliveryservice.payment.domain.repository.PaymentRepository;
import com.opportunity.deliveryservice.payment.infrastructure.dto.TossConfirmRequest;
import com.opportunity.deliveryservice.payment.infrastructure.dto.TossPaymentResponse;
import com.opportunity.deliveryservice.payment.presentation.dto.request.CancelPaymentRequest;
import com.opportunity.deliveryservice.payment.presentation.dto.request.ConfirmPaymentRequest;
import com.opportunity.deliveryservice.payment.presentation.dto.request.IntentPaymentRequest;
import com.opportunity.deliveryservice.payment.presentation.dto.response.PaymentResponse;
import com.opportunity.deliveryservice.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;
	private final TossPaymentService tossPaymentService;

	/**
	 * 결제 대기
	 * @param orderId
	 */
	@Transactional
	public void intentPayment(UUID orderId, IntentPaymentRequest request, User user) {
		Order order = getOrder(orderId);
		validate(user, order);

		Payment newPayment = Payment.builder()
			.order(order)
			.amount(request.amount())
			.build();

		paymentRepository.save(newPayment);
	}

	/**
	 * 결제 승인
	 * @param request
	 */
	@Transactional(noRollbackFor = OpptyException.class)
	public void confirmPayment(ConfirmPaymentRequest request, User user) {
		Order order = getOrder(request.orderId());
		validate(user, order);

		Payment payment = paymentRepository.findByOrder(order);

		persistPaymentInfo(payment, request);

		processConfirmInNewTx(payment.getId(), request);
	}

	/**
	 * 결제 취소
	 * @param request
	 */
	@Transactional(noRollbackFor = OpptyException.class)
	public void cancelPayment(CancelPaymentRequest request, User user) {
		verifyPaymentAuthorization(user, request.paymentKey());

		processCancelInNewTx(request);
	}

	/**
	 * 결제 내역 조회
	 * @param orderId
	 * @return
	 */
	@Transactional(readOnly = true)
	public PaymentResponse getPayment(UUID orderId, User user) {
		Order order = getOrder(orderId);
		validate(user, order);

		Payment payment = paymentRepository.findByOrder(order);

		TossPaymentResponse response = tossPaymentService.getPaymentInfo(payment.getTossPaymentKey());
		return PaymentResponse.of(response, payment.getId());
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = OpptyException.class)
	public void processConfirmInNewTx(UUID paymentId, ConfirmPaymentRequest request) {
	    Payment payment = paymentRepository.findById(paymentId).orElseThrow(
	        () -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND)
	    );

	    TossConfirmRequest req = buildTossConfirmRequest(request);
	    try {
	        TossPaymentResponse res = tossPaymentService.confirm(req);
	        payment.setPaymentResult(res.getStatus(), res.getMethod(), res.getApprovedAt());
	        validateResponseOrThrow(res);
			payment.setPaymentErrorNull();
	    } catch (OpptyException e) {
			if(payment.getStatus() != PaymentStatus.DONE) {
				payment.setPaymentError(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
			}
	        throw e;
	    }
	}


	@Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = OpptyException.class)
	public void processCancelInNewTx(CancelPaymentRequest request) {
		Payment payment = paymentRepository.findByTossPaymentKey(request.paymentKey()).orElseThrow(
			() -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND)
		);

		try {
			TossPaymentResponse res = tossPaymentService.cancel(request.paymentKey(), request);
			payment.setPaymentCancelInfo(
				res.getStatus(),
				res.getCancels().get(res.getCancels().size() - 1).getCanceledAt(),
				res.getCancels().get(res.getCancels().size() - 1).getCancelReason()
			);
			payment.setPaymentCancelErrorNull();
		} catch (OpptyException e) {
			payment.setPaymentCancelError(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
			throw e;
		}
	}

	private void validate(User user, Order order){
		if(!user.equals(order.getUser())){
			throw new OpptyException(ClientErrorCode.FORBIDDEN);
		}
	}

	private void verifyPaymentAuthorization(User user, String paymentKey){
		Payment payment = paymentRepository.findByTossPaymentKey(paymentKey).orElseThrow(
			() -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND)
		);

		if(!user.getId().toString().equals(payment.getCreatedBy())){
			throw new OpptyException(ClientErrorCode.FORBIDDEN);
		}
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


	private void validateResponseOrThrow(TossPaymentResponse res) {
		if (!PaymentStatus.DONE.toString().equalsIgnoreCase(res.getStatus())) {
			throw new OpptyException(ServerErrorCode.INVALID_PAYMENT_STATUS);
		}
	}

	private Order getOrder(UUID orderId) {
		return orderRepository.findById(orderId).orElseThrow(
			() -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND)
		);
	}

}
