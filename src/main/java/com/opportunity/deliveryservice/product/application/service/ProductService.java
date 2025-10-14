package com.opportunity.deliveryservice.product.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity.deliveryservice.gemini.application.service.GeminiService;
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
	private final ProductRepository productRepository;
	private final GeminiService geminiService;

	@Transactional
	public void createProduct(CreateProductRequest request) {
		validate();

		Product newProduct = Product.builder()
			.title(request.title())
			.price(request.price())
			.description(request.description())
			.image(request.image())
			.category(request.category())
			.build();

		if(request.useAI()){
			String description = generateDescription(request.aiPrompt(), newProduct);
			newProduct.setDescription(description);
		}

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


	private String generateDescription(String prompt, Product product){
		if (prompt == null || prompt.isBlank()) {
			throw new OpptyException(ClientErrorCode.INVALID_INPUT_VALUE);
		}

		return geminiService.createProductDescription(prompt, product);
	}

	private void validate(){
		//todo 가게id 및 사용자id 검증
	}

}
