package com.opportunity.deliveryservice.order.application.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.order.domain.entity.Order;
import com.opportunity.deliveryservice.order.domain.entity.OrderProduct;
import com.opportunity.deliveryservice.order.domain.entity.OrderProgress;
import com.opportunity.deliveryservice.order.domain.repository.OrderProductRepository;
import com.opportunity.deliveryservice.order.domain.repository.OrderRepository;
import com.opportunity.deliveryservice.order.presentation.dto.request.ChangeOrderProgressRequest;
import com.opportunity.deliveryservice.order.presentation.dto.request.CreateOrderRequest;
import com.opportunity.deliveryservice.payment.application.service.PaymentService;
import com.opportunity.deliveryservice.payment.domain.entity.Payment;
import com.opportunity.deliveryservice.payment.domain.repository.PaymentRepository;
import com.opportunity.deliveryservice.product.domain.entity.Product;
import com.opportunity.deliveryservice.product.domain.repository.ProductRepository;
import com.opportunity.deliveryservice.store.application.service.StoreService;
import com.opportunity.deliveryservice.store.presentation.dto.response.StoreGetRes;
import com.opportunity.deliveryservice.store.domain.entity.Store;
import com.opportunity.deliveryservice.store.domain.repository.StoreRepository;
import com.opportunity.deliveryservice.user.domain.entity.User;
import com.opportunity.deliveryservice.user.domain.entity.UserRoleEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;
	private final OrderProductRepository orderProductRepository;
	private final PaymentService paymentService;
	private final PaymentRepository paymentRepository;
	private final StoreService storeService;
	private final StoreRepository storeRepository;
  
	@Transactional
	public void createOrder(CreateOrderRequest req, User user) {
		Store store = storeRepository.findById(req.storeId()).orElseThrow(
			() -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND)
		);
		validateMinPrice(store, req.amount());

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

	@Transactional
	public void cancelOrder(UUID orderId, String cancelReason, User user) {
		Order order = orderRepository.findById(orderId).orElseThrow(
			() -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND)
		);

		Payment payment = paymentRepository.findByOrder(order);

		validateCancellable(order, user);

		paymentService.cancelPayment(payment.getTossPaymentKey(), cancelReason, user);
		order.changeProgress(OrderProgress.CANCELED);
	}

	private void validateMinPrice(Store store, Integer amount){
		if(store.getMinOrderPrice() > amount){
			throw new OpptyException(ClientErrorCode.ORDER_MIN_PRICE_NOT_MET);
		}
	}

	private void validateCancellable(Order order, User user){
		if(user.getRole().equals(UserRoleEnum.CUSTOMER) && !order.getUser().equals(user)){
			throw new OpptyException(ClientErrorCode.FORBIDDEN);
		}

		// 가게 사장도 취소 가능
		if(user.getRole().equals(UserRoleEnum.OWNER) && !order.getStore().getUserId().equals(user.getId())){
			throw new OpptyException(ClientErrorCode.FORBIDDEN);
		}

		if(order.getProgress().equals(OrderProgress.ORDER_CONFIRMED)){
			throw new OpptyException(ClientErrorCode.ORDER_ALREADY_CONFIRMED);
		}

		if (Duration.between(order.getCreatedAt(), LocalDateTime.now()).toMinutes() > 5) {
			throw new OpptyException(ClientErrorCode.CANCELLATION_TIME_EXPIRED);
		}
	}

	@Transactional
	public void changeProgress(UUID orderId, ChangeOrderProgressRequest request, User user) {
		Order order = orderRepository.findById(orderId).orElseThrow(
			() -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND)
		);

		if(!storeService.checkOwner(request.storeId(), user.getId())){
			throw new OpptyException(ClientErrorCode.NOT_STORE_OWNER);
		}

		order.changeProgress(OrderProgress.valueOf(request.orderProgress()));
	}
}
