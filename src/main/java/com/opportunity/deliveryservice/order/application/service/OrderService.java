package com.opportunity.deliveryservice.order.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.order.domain.entity.Order;
import com.opportunity.deliveryservice.order.domain.entity.OrderProduct;
import com.opportunity.deliveryservice.order.domain.repository.OrderProductRepository;
import com.opportunity.deliveryservice.order.domain.repository.OrderRepository;
import com.opportunity.deliveryservice.order.presentation.dto.request.CreateOrderRequest;
import com.opportunity.deliveryservice.payment.domain.entity.Payment;
import com.opportunity.deliveryservice.payment.domain.repository.PaymentRepository;
import com.opportunity.deliveryservice.product.domain.entity.Product;
import com.opportunity.deliveryservice.product.domain.repository.ProductRepository;
import com.opportunity.deliveryservice.user.domain.entity.User;
import com.opportunity.deliveryservice.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;
	private final OrderProductRepository orderProductRepository;

	@Transactional
	public void createOrder(CreateOrderRequest req, User user) {
		Order newOrder = Order.builder()
			.amount(req.amount())
			.user(user)
			.request(req.request())
			.build();

		orderRepository.save(newOrder);

		List<OrderProduct> orderProducts = req.productList().stream()
			.map(info -> {
				Product foundProduct = getProduct(info.productId());
				return OrderProduct.builder()
					.order(newOrder)
					.productId(foundProduct.getId())
					.productPrice(foundProduct.getPrice())
					.productQuantity(info.quantity())
					.productTitle(foundProduct.getTitle())
					.build();
			})
			.toList();

		orderProductRepository.saveAll(orderProducts);
	}

	private Product getProduct(UUID productId){
		return productRepository.findById(productId).orElseThrow(
			() -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND)
		);
	}


}
