package com.opportunity.deliveryservice.order.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.opportunity.deliveryservice.order.domain.entity.Order;
import com.opportunity.deliveryservice.order.domain.entity.OrderProgress;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResOrderListDto {
	private UUID orderId;
	private Integer amount; // 총 주문 금액
	private OrderProgress orderProgress;
	private LocalDateTime createdAt;
	private ResOrderProductDto product;

	public static ResOrderListDto fromEntity(Order order, ResOrderProductDto product) {
		return ResOrderListDto.builder()
			.orderId(order.getId())
			.amount(order.getAmount())
			.orderProgress(order.getProgress())
			.createdAt(order.getCreatedAt())
			.product(product)
			.build();
	}
}
