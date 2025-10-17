package com.opportunity.deliveryservice.order.application.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.global.infrastructure.config.security.UserDetailsImpl;
import com.opportunity.deliveryservice.order.domain.entity.Order;
import com.opportunity.deliveryservice.order.domain.entity.OrderProduct;
import com.opportunity.deliveryservice.order.domain.repository.OrderPaymentRepository;
import com.opportunity.deliveryservice.order.domain.repository.OrderProductRepository;
import com.opportunity.deliveryservice.order.domain.repository.OrderRepository;
import com.opportunity.deliveryservice.order.presentation.dto.response.ResOrderDetailDto;
import com.opportunity.deliveryservice.order.presentation.dto.response.ResOrderListDto;
import com.opportunity.deliveryservice.order.presentation.dto.response.ResOrderProductDto;
import com.opportunity.deliveryservice.payment.domain.entity.Payment;
import com.opportunity.deliveryservice.user.domain.entity.UserRoleEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceV1 {
	private final OrderRepository orderRepository;
	private final OrderProductRepository orderProductRepository;
	private final OrderPaymentRepository orderPaymentRepository;

	// 주문 내역 조회
	public List<ResOrderListDto> getOrders(UserDetailsImpl user) {
		// 권한에 따라 조회 메서드 선택
		List<Order> orders;

		if (user.getUser().getRole() == UserRoleEnum.MASTER) {
			// MASTER: 모든 주문 목록 조회
			orders = orderRepository.findAllByOrderByCreatedAtDesc();
		} else if (user.getUser().getRole() == UserRoleEnum.CUSTOMER) {
			// CUSTOMER: 자신의 주문 목록 조회
			orders = orderRepository.findAllByUserIdOrderByCreatedAtDesc(user.getUser().getId());
		} else {
			throw new OpptyException(ClientErrorCode.FORBIDDEN);
		}

		// 해당 주문 목록의 orderProduct 조회
		List<UUID> orderIds = orders.stream().map(Order::getId).toList();
		List<OrderProduct> orderProducts = orderProductRepository.findAllByOrder_In(orderIds);

		// orderId -> OrderProduct (주문id -> 상품 정보) 매핑
		Map<UUID, OrderProduct> opByOrderId = orderProducts.stream()
			.collect(Collectors.toMap(OrderProduct::getOrderId, Function.identity()));

		// dto로 반환
		return orders.stream()
			.map(o -> {
				OrderProduct orderProduct = opByOrderId.get(o.getId());
				ResOrderProductDto productDto = orderProduct != null ? ResOrderProductDto.fromEntity(orderProduct) : null;
				return ResOrderListDto.fromEntity(o,productDto);
			})
			.toList();
	}

	// 주문 상세 내역 조회
	public ResOrderDetailDto getOrderDetail(UUID orderId, UserDetailsImpl user) {
		// 권한에 따라 조회 메서드 선택
		Order order;

		if (user.getUser().getRole() == UserRoleEnum.MASTER) {
			// MASTER: 모든 주문 목록 조회
			order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND));
		} else if (user.getUser().getRole() == UserRoleEnum.CUSTOMER) {
			// CUSTOMER: 자신의 주문 목록 조회
			order = orderRepository.findByIdAndUserId(orderId, user.getUser().getId())
				.orElseThrow(() -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND));
		} else {
			throw new OpptyException(ClientErrorCode.FORBIDDEN);
		}

		// 결제 정보 조회
		Payment payment = orderPaymentRepository.findByOrder(order)
			.orElse(null); // 결제 정보가 없는 주문

		// 상품 정보 조회
		OrderProduct orderProduct = orderProductRepository.findByOrder(order)
			.orElseThrow(() -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND));

		return ResOrderDetailDto.fromEntity(order, orderProduct, payment);
	}
}
