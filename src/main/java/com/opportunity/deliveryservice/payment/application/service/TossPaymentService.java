package com.opportunity.deliveryservice.payment.application.service;

import org.springframework.stereotype.Service;

import com.opportunity.deliveryservice.global.common.code.DynamicErrorCode;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.global.common.exception.TossRemoteException;
import com.opportunity.deliveryservice.payment.infrastructure.TossPaymentsClient;
import com.opportunity.deliveryservice.payment.infrastructure.dto.TossCancelRequest;
import com.opportunity.deliveryservice.payment.infrastructure.dto.TossConfirmRequest;
import com.opportunity.deliveryservice.payment.infrastructure.dto.TossPaymentResponse;
import com.opportunity.deliveryservice.payment.presentation.dto.request.CancelPaymentRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentService {

	private final TossPaymentsClient tossPaymentsClient;

	public TossPaymentResponse confirm(TossConfirmRequest tossConfirmRequest) {
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

	public TossPaymentResponse getPaymentInfo(String tossPaymentKey) {
		try {
			return tossPaymentsClient.getPaymentInfo(tossPaymentKey);
		} catch (TossRemoteException e) {
			log.warn("[PAY] Toss getPaymentInfo failed: status={}, code={}, msg={}", e.getStatus(), e.getCode(),
				e.getMessage());

			String clientMsg = e.getTossMessage() != null ? e.getTossMessage() : "결제 조회 중 오류가 발생했습니다.";
			String clientCode = e.getCode() != null ? e.getCode() : "TOSS_UNKNOWN_ERROR";

			throw new OpptyException(new DynamicErrorCode(e.getStatus(), clientCode, clientMsg));
		}
	}


	public TossPaymentResponse cancel(String tossPaymentKey, CancelPaymentRequest request) {
		try {
			return tossPaymentsClient.cancel(tossPaymentKey, TossCancelRequest.of(request.cancelReason()));
		} catch (TossRemoteException e) {
			log.warn("[PAY] Toss cacel failed: status={}, code={}, msg={}", e.getStatus(), e.getCode(),
				e.getMessage());

			String clientMsg = e.getTossMessage() != null ? e.getTossMessage() : "결제 취소 중 오류가 발생했습니다.";
			String clientCode = e.getCode() != null ? e.getCode() : "TOSS_UNKNOWN_ERROR";

			throw new OpptyException(new DynamicErrorCode(e.getStatus(), clientCode, clientMsg));
		}
	}
}
