package com.opportunity.deliveryservice.product.application.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import com.opportunity.deliveryservice.gemini.application.service.GeminiService;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.product.domain.entity.Product;
import com.opportunity.deliveryservice.product.domain.entity.ProductCategory;
import com.opportunity.deliveryservice.product.domain.repository.ProductRepository;
import com.opportunity.deliveryservice.product.presentation.dto.request.CreateProductRequest;
import com.opportunity.deliveryservice.product.presentation.dto.request.UpdateProductRequest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Disabled
class ProductServiceTest {

	@Mock ProductRepository productRepository;
	@Mock GeminiService geminiService;

	@InjectMocks ProductService productService;

	@Test
	@DisplayName("createProduct: useAI=true 이면 Gemini로 설명 생성 후 저장")
	void createProduct_useAI_callsGemini_andSave() {
		// given
		CreateProductRequest req = mock(CreateProductRequest.class);
		when(req.title()).thenReturn("치킨");
		when(req.price()).thenReturn(19000L);
		when(req.description()).thenReturn(null);
		when(req.image()).thenReturn("http://img");
		when(req.category()).thenReturn(ProductCategory.CHICKEN);
		when(req.useAI()).thenReturn(true);
		when(req.AiPrompt()).thenReturn("매콤달콤 치킨");

		when(geminiService.createProductDescription(eq("매콤달콤 치킨"), any(Product.class)))
			.thenReturn("AI가 생성한 설명");

		// when
		productService.createProduct(req);

		// then
		verify(geminiService, times(1))
			.createProductDescription(eq("매콤달콤 치킨"), any(Product.class));
		verify(productRepository, times(1)).save(any(Product.class));
	}

	@Test
	@DisplayName("createProduct: useAI=false 이면 Gemini 호출하지 않고 저장")
	void createProduct_notUseAI_noGemini_andSave() {
		// given
		CreateProductRequest req = mock(CreateProductRequest.class);
		when(req.title()).thenReturn("피자");
		when(req.price()).thenReturn(23000L);
		when(req.description()).thenReturn("치즈가득");
		when(req.image()).thenReturn("http://img2");
		when(req.category()).thenReturn(ProductCategory.PIZZA);
		when(req.useAI()).thenReturn(false);

		// when
		productService.createProduct(req);

		// then
		verify(geminiService, never()).createProductDescription(anyString(), any(Product.class));
		verify(productRepository, times(1)).save(any(Product.class));
	}

	@Test
	@DisplayName("createProduct: useAI=true & prompt가 비어있으면 OpptyException")
	void createProduct_useAI_blankPrompt_throw() {
		// given
		CreateProductRequest req = mock(CreateProductRequest.class);
		when(req.title()).thenReturn("분식");
		when(req.price()).thenReturn(8000L);
		when(req.description()).thenReturn("떡볶이");
		when(req.image()).thenReturn("http://img3");
		when(req.category()).thenReturn(ProductCategory.KOREAN);
		when(req.useAI()).thenReturn(true);
		when(req.AiPrompt()).thenReturn("  "); // blank

		// when & then
		assertThrows(OpptyException.class, () -> productService.createProduct(req));
		verify(geminiService, never()).createProductDescription(anyString(), any(Product.class));
		verify(productRepository, never()).save(any(Product.class));
	}


	@Test
	@DisplayName("updateProduct: 존재하는 상품이면 도메인 메서드 호출")
	void updateProduct_success() {
		// given
		UUID id = UUID.randomUUID();
		Product product = mock(Product.class);
		when(productRepository.findById(id)).thenReturn(Optional.of(product));

		UpdateProductRequest req = mock(UpdateProductRequest.class);
		when(req.title()).thenReturn("수정된 제목");
		when(req.description()).thenReturn("수정된 설명");
		when(req.price()).thenReturn(9999L);
		when(req.category()).thenReturn(ProductCategory.CHICKEN);
		when(req.image()).thenReturn("http://new");

		// when
		productService.updateProduct(req, id);

		// then
		verify(product).updateProduct("수정된 제목", "수정된 설명", 9999L, ProductCategory.CHICKEN, "http://new");
	}

	@Test
	@DisplayName("deleteProduct: softDelete 호출")
	void deleteProduct_success() {
		// given
		UUID id = UUID.randomUUID();
		Product product = mock(Product.class);
		when(productRepository.findById(id)).thenReturn(Optional.of(product));

		// when
		productService.deleteProduct(id, 77L);

		// then
		verify(product).softDelete(77L);
	}

	@Test
	@DisplayName("updateProductVisibility: changeVisible 호출")
	void updateProductVisibility_success() {
		// given
		UUID id = UUID.randomUUID();
		Product product = mock(Product.class);
		when(productRepository.findById(id)).thenReturn(Optional.of(product));

		// when
		productService.updateProductVisibility(id);

		// then
		verify(product).changeVisible();
	}

	@Test
	@DisplayName("getProductDetail: 존재하면 해당 Product 반환")
	void getProductDetail_success() {
		// given
		UUID id = UUID.randomUUID();
		Product product = mock(Product.class);
		when(productRepository.findById(id)).thenReturn(Optional.of(product));

		// when
		Product found = productService.getProductDetail(id);

		// then
		assertSame(product, found);
	}

	@Nested
	class NotFoundCases {
		@Test
		@DisplayName("update/get/delete/visibility: 찾지 못하면 OpptyException(RESOURCE_NOT_FOUND)")
		void notFound_throw() {
			UUID id = UUID.randomUUID();
			when(productRepository.findById(id)).thenReturn(Optional.empty());

			// update
			UpdateProductRequest upReq = mock(UpdateProductRequest.class);
			assertThrows(OpptyException.class, () -> productService.updateProduct(upReq, id));

			// get
			assertThrows(OpptyException.class, () -> productService.getProductDetail(id));

			// delete
			assertThrows(OpptyException.class, () -> productService.deleteProduct(id, 1L));

			// visibility
			assertThrows(OpptyException.class, () -> productService.updateProductVisibility(id));
		}
	}
}