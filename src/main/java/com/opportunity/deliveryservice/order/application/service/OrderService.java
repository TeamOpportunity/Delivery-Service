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
import com.opportunity.deliveryservice.product.domain.entity.Product;
import com.opportunity.deliveryservice.product.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;
	private final OrderProductRepository orderProductRepository;

	@Transactional
	public void createOrder(CreateOrderRequest req) {
		Order newOrder = Order.builder()
			.amount(req.amount())
			.request(req.request())
			.build();

		List<OrderProduct> orderProducts = req.productList().stream()
			.map(info -> {
				Product foundProduct = getProduct(info.productId());
				return OrderProduct.builder()
					.productId(foundProduct.getId())
					.productPrice(foundProduct.getPrice())
					.productQuantity(info.quantity())
					.productTitle(foundProduct.getTitle())
					.build();
			})
			.toList();

		orderRepository.save(newOrder);
		orderProductRepository.saveAll(orderProducts);
	}

	private Product getProduct(UUID productId){
		return productRepository.findById(productId).orElseThrow(
			() -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND)
		);
	}


}
