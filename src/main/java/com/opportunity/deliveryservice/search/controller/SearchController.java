package com.opportunity.deliveryservice.search.controller;

import com.opportunity.deliveryservice.global.common.response.ApiResponse;
import com.opportunity.deliveryservice.search.dto.response.ProductSearchPageDto;
import com.opportunity.deliveryservice.search.dto.response.SearchResultDto;
import com.opportunity.deliveryservice.search.service.SearchService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

	private final SearchService searchService;

	@GetMapping
	public ResponseEntity<ApiResponse<SearchResultDto>> search(
		@RequestParam String keyword,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		SearchResultDto result = searchService.search(keyword, pageable);
		return ResponseEntity.ok(ApiResponse.success(result));
	}

	@GetMapping("/stores")
	public ResponseEntity<ApiResponse<SearchResultDto>> searchStores(
		@RequestParam String keyword,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		SearchResultDto result = searchService.searchStores(keyword, pageable);
		return ResponseEntity.ok(ApiResponse.success(result));
	}

	@GetMapping("/products")
	public ResponseEntity<ApiResponse<ProductSearchPageDto>> searchProducts(
		@RequestParam String keyword,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		ProductSearchPageDto result = searchService.searchProducts(keyword, pageable);
		return ResponseEntity.ok(ApiResponse.success(result));
	}
}
