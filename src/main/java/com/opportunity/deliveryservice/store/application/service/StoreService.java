package com.opportunity.deliveryservice.store.application.service;

import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.product.domain.entity.Product;
import com.opportunity.deliveryservice.product.domain.repository.ProductRepository;
import com.opportunity.deliveryservice.store.domain.entity.Category;
import com.opportunity.deliveryservice.store.domain.entity.Store;
import com.opportunity.deliveryservice.store.domain.repository.CategoryRepository;
import com.opportunity.deliveryservice.store.domain.repository.StoreRepository;
import com.opportunity.deliveryservice.store.presentation.dto.request.StoreCreateReq;
import com.opportunity.deliveryservice.store.presentation.dto.request.StoreUpdateReq;
import com.opportunity.deliveryservice.store.presentation.dto.response.StoreGetRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.opportunity.deliveryservice.global.common.code.ClientErrorCode.FORBIDDEN;
import static com.opportunity.deliveryservice.global.common.code.ClientErrorCode.RESOURCE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Transactional
    public StoreGetRes createStore(Long userId, StoreCreateReq request) {
        List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
        if (categories.size() != request.getCategoryIds().size()) {
            throw new OpptyException(RESOURCE_NOT_FOUND);
        }

        Store store = Store.builder()
                .userId(userId)
                .city(request.getCity())
                .gu(request.getGu())
                .detailAddress(request.getDetailAddress())
                .content(request.getContent())
                .name(request.getName())
                .minOrderPrice(request.getMinOrderPrice())
                .startTime(LocalTime.parse(request.getStartTime(), TIME_FORMATTER))
                .endTime(LocalTime.parse(request.getEndTime(), TIME_FORMATTER))
                .build();

        categories.forEach(store::addCategory);

        Store savedStore = storeRepository.save(store);
        return StoreGetRes.from(savedStore);
    }

    public StoreGetRes getStore(UUID storeId) {
        Store store = storeRepository.findByIdAndNotDeleted(storeId)
                .orElseThrow(() -> new OpptyException(RESOURCE_NOT_FOUND));

        return StoreGetRes.from(store);
    }

    public List<StoreGetRes> getStoresByUserId(Long userId) {
        List<Store> stores = storeRepository.findByUserIdAndNotDeleted(userId);
        return stores.stream()
                .map(StoreGetRes::from)
                .collect(Collectors.toList());
    }

    public List<StoreGetRes> getStoresByCategory(Integer categoryId) {
        List<Store> stores = storeRepository.findByCategoryIdAndNotDeleted(categoryId);
        return stores.stream()
                .map(StoreGetRes::from)
                .collect(Collectors.toList());
    }

    public List<StoreGetRes> getStoresByLocation(String city, String gu) {
        List<Store> stores = storeRepository.findByCityAndGuAndNotDeleted(city, gu);
        return stores.stream()
                .map(StoreGetRes::from)
                .collect(Collectors.toList());
    }

    public List<StoreGetRes> searchStoresByName(String name) {
        List<Store> stores = storeRepository.findByNameContainingAndNotDeleted(name);
        return stores.stream()
                .map(StoreGetRes::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public StoreGetRes updateStore(UUID storeId, Long userId, StoreUpdateReq request) {
        Store store = storeRepository.findByIdAndNotDeleted(storeId)
                .orElseThrow(() -> new OpptyException(RESOURCE_NOT_FOUND));

        // 가게 소유자 확인
        if (!store.getUserId().equals(userId)) {
            throw new OpptyException(FORBIDDEN);
        }

        // 카테고리 조회
        List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
        if (categories.size() != request.getCategoryIds().size()) {
            throw new OpptyException(RESOURCE_NOT_FOUND);
        }

        // 가게 정보 수정
        store.update(
                request.getCity(),
                request.getGu(),
                request.getDetailAddress(),
                request.getContent(),
                request.getName(),
                request.getMinOrderPrice(),
                LocalTime.parse(request.getStartTime(), TIME_FORMATTER),
                LocalTime.parse(request.getEndTime(), TIME_FORMATTER)
        );

        // 카테고리 업데이트
        store.updateCategories(categories);

        return StoreGetRes.from(store);
    }

    /**
     * 가게 삭제 (소프트 삭제)
     * 가게 삭제 시 해당 가게의 모든 상품도 함께 소프트 삭제
     */
    @Transactional
    public void deleteStore(UUID storeId, Long userId) {
        Store store = storeRepository.findByIdAndNotDeleted(storeId)
                .orElseThrow(() -> new OpptyException(RESOURCE_NOT_FOUND));

        // 가게 소유자 확인
        if (!store.getUserId().equals(userId)) {
            throw new OpptyException(FORBIDDEN);
        }

        // 1. 해당 가게의 모든 상품 조회 (삭제된 것도 포함 - 재삭제 방지)
        List<Product> products = productRepository.findAllByStoreId(storeId);

        // 2. 아직 삭제되지 않은 상품들만 소프트 삭제
        long deletedProductCount = products.stream()
                .filter(product -> product.getDeletedAt() == null)
                .peek(product -> product.softDelete(userId))
                .count();

        log.info("가게 삭제 - 가게 ID: {}, 함께 삭제된 상품 수: {}", storeId, deletedProductCount);

        // 3. 가게 소프트 삭제
        store.delete(userId);
    }
}
