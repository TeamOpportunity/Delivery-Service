package com.opportunity.deliveryservice.search.dto.response;

import com.opportunity.deliveryservice.store.domain.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreSearchResultDto {

    private UUID id;
    private String name;
    private String city;
    private String gu;
    private String detailAddress;
    private String content;
    private int minOrderPrice;
    private LocalTime startTime;
    private LocalTime endTime;
    private String type; // "STORE"

    @Builder.Default
    private List<ProductSearchResultDto> products = new ArrayList<>();

    public static StoreSearchResultDto from(Store store) {
        return StoreSearchResultDto.builder()
                .id(store.getId())
                .name(store.getName())
                .city(store.getCity())
                .gu(store.getGu())
                .detailAddress(store.getDetailAddress())
                .content(store.getContent())
                .minOrderPrice(store.getMinOrderPrice())
                .startTime(store.getStartTime())
                .endTime(store.getEndTime())
                .type("STORE")
                .products(new ArrayList<>())
                .build();
    }

    public static StoreSearchResultDto from(Store store, List<ProductSearchResultDto> products) {
        return StoreSearchResultDto.builder()
                .id(store.getId())
                .name(store.getName())
                .city(store.getCity())
                .gu(store.getGu())
                .detailAddress(store.getDetailAddress())
                .content(store.getContent())
                .minOrderPrice(store.getMinOrderPrice())
                .startTime(store.getStartTime())
                .endTime(store.getEndTime())
                .type("STORE")
                .products(products)
                .build();
    }
}
