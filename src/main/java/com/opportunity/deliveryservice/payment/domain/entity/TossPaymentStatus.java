package com.opportunity.deliveryservice.payment.domain.entity;

public enum TossPaymentStatus {
    ABORTED, // 결제 실패
	CANCELED, // 결제 취소
	DONE, // 결제 승인
	EXPIRED, // 결제 유효시간 만료 (30분)
	IN_PROGRESS, // 결제수단 정보와 해당 결제수단의 소유자가 맞는지 인증을 마친 상태
	PARTIAL_CANCELED, // 승인된 결제가 부분 취소
	READY, // 결제 초기 단계, 인증 전
	WAITING_FOR_DEPOSIT; // 발급된 가상계좌에 구매자가 아직 입금하지 않은 상태 (가상계좌로 결제시)

	public static TossPaymentStatus from(String status) {
		return TossPaymentStatus.valueOf(status.trim().toUpperCase());
	}
}
