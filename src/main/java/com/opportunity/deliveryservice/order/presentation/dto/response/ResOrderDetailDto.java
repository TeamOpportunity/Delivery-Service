package com.opportunity.deliveryservice.order.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.opportunity.deliveryservice.order.domain.entity.Order;
import com.opportunity.deliveryservice.order.domain.entity.OrderProduct;
import com.opportunity.deliveryservice.order.domain.entity.OrderProgress;
import com.opportunity.deliveryservice.payment.domain.entity.Payment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResOrderDetailDto {
	private UUID orderId; // 주문 번호
	private Integer amount; // 총 주문 금액
	private OrderProgress orderProgress; // 주문 상태
	private LocalDateTime createdAt; // 주문 생성일
	private String request; // 요청사항
	private ResOrderProductDto product; // 주문 상품 정보
	private ResOrderPaymentDto payment; // 주문 결제 정보
	private String productImage; // 주문 상품 사진
	private UUID storeId; // 가게 id
	private String storeName; // 가게 이름

	public static ResOrderDetailDto fromEntity(Order order, OrderProduct orderProduct, Payment payment) {
		return ResOrderDetailDto.builder()
			.orderId(order.getId())
			.amount(order.getAmount())
			.orderProgress(order.getProgress())
			.createdAt(order.getCreatedAt())
			.request(order.getRequest())
			.product(orderProduct != null ? ResOrderProductDto.fromEntity(orderProduct) : null)
			.payment(payment != null ? ResOrderPaymentDto.fromEntity(payment) : null)
			.productImage(orderProduct.getProductImage())
			.storeId(orderProduct.getStoreId())
			.storeName(orderProduct.getStoreName())
			.build();
	}
}
