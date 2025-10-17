package com.opportunity.deliveryservice.store.presentation.dto.response;

import com.opportunity.deliveryservice.store.domain.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreGetRes {

    private UUID id;
    private Long userId;
    private List<CategoryDto> categories;
    private String city;
    private String gu;
    private String detailAddress;
    private String content;
    private String name;
    private int minOrderPrice;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class CategoryDto {
        private Integer id;
        private String name;
    }

    public static StoreGetRes from(Store store) {
        List<CategoryDto> categories = store.getStoreCategories().stream()
                .map(sc -> CategoryDto.builder()
                        .id(sc.getCategory().getId())
                        .name(sc.getCategory().getCategory())
                        .build())
                .collect(Collectors.toList());

        return StoreGetRes.builder()
                .id(store.getId())
                .userId(store.getUserId())
                .categories(categories)
                .city(store.getCity())
                .gu(store.getGu())
                .detailAddress(store.getDetailAddress())
                .content(store.getContent())
                .name(store.getName())
                .minOrderPrice(store.getMinOrderPrice())
                .startTime(store.getStartTime())
                .endTime(store.getEndTime())
                .createdAt(store.getCreatedAt())
                .updatedAt(store.getUpdatedAt())
                .build();
    }
}
