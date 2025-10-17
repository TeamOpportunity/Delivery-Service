package com.opportunity.deliveryservice.store.presentation.controller;

import com.opportunity.deliveryservice.global.common.response.ApiResponse;
import com.opportunity.deliveryservice.global.infrastructure.config.security.UserDetailsImpl;
import com.opportunity.deliveryservice.store.application.service.StoreService;
import com.opportunity.deliveryservice.store.presentation.dto.request.StoreCreateReq;
import com.opportunity.deliveryservice.store.presentation.dto.request.StoreUpdateReq;
import com.opportunity.deliveryservice.store.presentation.dto.response.StoreGetRes;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
@Tag(name = "Store", description = "가게 API")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<StoreGetRes>> createStore(
            @RequestBody StoreCreateReq request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();
        StoreGetRes response = storeService.createStore(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreGetRes>> getStore(
            @PathVariable UUID storeId) {

        StoreGetRes response = storeService.getStore(storeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<List<StoreGetRes>>> getMyStores(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();
        List<StoreGetRes> response = storeService.getStoresByUserId(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<StoreGetRes>>> getStoresByCategory(
            @PathVariable Integer categoryId) {

        List<StoreGetRes> response = storeService.getStoresByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/location")
    public ResponseEntity<ApiResponse<List<StoreGetRes>>> getStoresByLocation(
            @RequestParam String city,
            @RequestParam String gu) {

        List<StoreGetRes> response = storeService.getStoresByLocation(city, gu);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StoreGetRes>>> searchStores(
            @RequestParam String name) {

        List<StoreGetRes> response = storeService.searchStoresByName(name);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{storeId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<StoreGetRes>> updateStore(
            @PathVariable UUID storeId,
            @RequestBody StoreUpdateReq request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();
        StoreGetRes response = storeService.updateStore(storeId, userId, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Void>> deleteStore(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();
        storeService.deleteStore(storeId, userId);

        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
