package com.opportunity.deliveryservice.product.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity.deliveryservice.global.common.code.BaseErrorCode;
import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.product.domain.entity.Product;
import com.opportunity.deliveryservice.product.domain.repository.ProductRepository;
import com.opportunity.deliveryservice.product.presentation.dto.request.CreateProductRequest;
import com.opportunity.deliveryservice.product.presentation.dto.request.UpdateProductRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
	private static ProductRepository productRepository;


	@Transactional
	public void createProduct(CreateProductRequest request) {
		validate();

		String description = request.useAI() ? generateDescription(request.AiPrompt()) :request.description();

		Product newProduct = Product.builder()
			.title(request.title())
			.price(request.price())
			.description(description)
			.image(request.image())
			.category(request.category())
			.build();

		productRepository.save(newProduct);
	}

	@Transactional
	public void updateProduct(UpdateProductRequest request, UUID productId) {
		validate();

		Product product = getProduct(productId);
		product.updateProduct(request.title(), request.description(), request.price(), request.category(), request.image());
	}

	@Transactional
	public void deleteProduct(UUID productId, Long userId) {
		validate();

		Product product = getProduct(productId);
		product.softDelete(userId);
	}
	@Transactional
	public void updateProductVisibility(UUID productId) {
		validate();

		Product product = getProduct(productId);
		product.changeVisible();
	}

	@Transactional(readOnly = true)
	public Product getProductDetail(UUID productId) {
		validate();

		return getProduct(productId);
	}

	private Product getProduct(UUID productId){
		return productRepository.findById(productId).orElseThrow(
			() -> new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND)
		);
	}


	private String generateDescription(String prompt){
		if (prompt == null || prompt.isBlank()) {
			throw new OpptyException(ClientErrorCode.INVALID_INPUT_VALUE);
		}

		//todo ai 사용
		return "";
	}

	private void validate(){
		//todo 가게id 및 사용자id 검증
	}

}
