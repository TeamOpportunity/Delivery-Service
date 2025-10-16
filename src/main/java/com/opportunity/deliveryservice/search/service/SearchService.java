package com.opportunity.deliveryservice.search.service;

import com.opportunity.deliveryservice.product.domain.entity.Product;
import com.opportunity.deliveryservice.product.domain.repository.ProductRepository;
import com.opportunity.deliveryservice.search.dto.response.ProductSearchResultDto;
import com.opportunity.deliveryservice.search.dto.response.SearchResultDto;
import com.opportunity.deliveryservice.search.dto.response.StoreSearchResultDto;
import com.opportunity.deliveryservice.store.domain.entity.Store;
import com.opportunity.deliveryservice.store.domain.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 통합 검색 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    public SearchResultDto search(String keyword, Pageable pageable) {
        log.info("통합 검색 시작 - 키워드: {}, 페이지: {}, 크기: {}",
                keyword, pageable.getPageNumber(), pageable.getPageSize());

        Page<Store> storesPage = storeRepository.findByNameContainingAndNotDeleted(keyword, pageable);
        List<Store> storesByName = storesPage.getContent();

        List<UUID> storeIds = storesByName.stream()
                .map(Store::getId)
                .collect(Collectors.toList());

        List<Product> productsForStores = storeIds.isEmpty() ?
                new ArrayList<>() :
                productRepository.searchByTitleAndStoreIds(keyword, storeIds);

        Map<UUID, List<ProductSearchResultDto>> productsByStoreId = productsForStores.stream()
                .filter(product -> product.getStoreId() != null)
                .map(ProductSearchResultDto::from)
                .collect(Collectors.groupingBy(ProductSearchResultDto::getStoreId));

        List<Product> productsFromOtherStores = storeIds.isEmpty() ?
                productRepository.searchByTitle(keyword) :
                productRepository.searchByTitleExcludingStoreIds(keyword, storeIds);

        Set<UUID> additionalStoreIds = productsFromOtherStores.stream()
                .map(Product::getStoreId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Store> additionalStores = additionalStoreIds.isEmpty() ?
                new ArrayList<>() :
                storeRepository.findAllByIdAndNotDeleted(new ArrayList<>(additionalStoreIds));

        productsFromOtherStores.stream()
                .filter(product -> product.getStoreId() != null)
                .map(ProductSearchResultDto::from)
                .forEach(productDto -> {
                    productsByStoreId
                            .computeIfAbsent(productDto.getStoreId(), k -> new ArrayList<>())
                            .add(productDto);
                });

        Map<UUID, Store> allStoresMap = new LinkedHashMap<>();
        storesByName.forEach(store -> allStoresMap.put(store.getId(), store));
        additionalStores.forEach(store -> allStoresMap.put(store.getId(), store));

        List<StoreSearchResultDto> storeResults = allStoresMap.values().stream()
                .filter(store -> store.getDeletedAt() == null)
                .map(store -> {
                    List<ProductSearchResultDto> matchedProducts =
                            productsByStoreId.getOrDefault(store.getId(), new ArrayList<>());
                    return StoreSearchResultDto.from(store, matchedProducts);
                })
                .collect(Collectors.toList());

        log.info("검색 완료 - 총 가게: {}개 (전체: {}개, 페이지: {}/{})",
                storeResults.size(), storesPage.getTotalElements(),
                pageable.getPageNumber() + 1, storesPage.getTotalPages());

        return SearchResultDto.of(storeResults,
                storesPage.getTotalElements(),
                storesPage.getTotalPages(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    public SearchResultDto search(String keyword) {
        log.info("통합 검색 시작 (페이징 없음) - 키워드: {}", keyword);

        List<Store> storesByName = storeRepository.findByNameContainingAndNotDeleted(keyword);

        List<Product> productsByTitle = productRepository.searchByTitle(keyword);

        Map<UUID, List<ProductSearchResultDto>> productsByStoreId = productsByTitle.stream()
                .filter(product -> product.getStoreId() != null)
                .map(ProductSearchResultDto::from)
                .collect(Collectors.groupingBy(ProductSearchResultDto::getStoreId));

        Set<UUID> storeIdsWithProducts = new HashSet<>(productsByStoreId.keySet());

        List<UUID> additionalStoreIds = storeIdsWithProducts.stream()
                .filter(storeId -> storesByName.stream().noneMatch(store -> store.getId().equals(storeId)))
                .collect(Collectors.toList());

        List<Store> additionalStores = new ArrayList<>();
        if (!additionalStoreIds.isEmpty()) {
            additionalStores = storeRepository.findAllByIdAndNotDeleted(additionalStoreIds);
        }

        Map<UUID, Store> allStoresMap = new HashMap<>();
        storesByName.forEach(store -> allStoresMap.put(store.getId(), store));
        additionalStores.forEach(store -> allStoresMap.put(store.getId(), store));

        List<StoreSearchResultDto> storeResults = allStoresMap.values().stream()
                .filter(store -> store.getDeletedAt() == null)
                .map(store -> {
                    List<ProductSearchResultDto> matchedProducts = productsByStoreId.getOrDefault(store.getId(), new ArrayList<>());
                    return StoreSearchResultDto.from(store, matchedProducts);
                })
                .collect(Collectors.toList());

        log.info("검색 완료 - 총 가게: {}개", storeResults.size());

        return SearchResultDto.of(storeResults);
    }

    public List<StoreSearchResultDto> searchStores(String keyword) {
        log.info("가게 검색 시작 - 키워드: {}", keyword);

        List<Store> stores = storeRepository.findByNameContainingAndNotDeleted(keyword);
        return stores.stream()
                .map(StoreSearchResultDto::from)
                .collect(Collectors.toList());
    }

    public List<ProductSearchResultDto> searchProducts(String keyword) {
        log.info("상품 검색 시작 - 키워드: {}", keyword);

        List<Product> products = productRepository.searchByTitle(keyword);
        return products.stream()
                .map(ProductSearchResultDto::from)
                .collect(Collectors.toList());
    }
}
