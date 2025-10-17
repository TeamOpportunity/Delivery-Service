package com.opportunity.deliveryservice.order.domain.entity;

public enum OrderProgress {
	ORDER_REQUESTED,   // 주문 접수 (결제 완료 후)
	ORDER_CONFIRMED,   // 가게가 주문을 수락함
	COOKING,           // 조리 중
	READY_FOR_PICKUP,  // 배달 픽업 대기 (조리 완료)
	PICKED_UP,         // 배달원이 픽업함
	DELIVERING,        // 배달 중
	DELIVERED,         // 배달 완료
	CANCELED           // 주문 취소
}
