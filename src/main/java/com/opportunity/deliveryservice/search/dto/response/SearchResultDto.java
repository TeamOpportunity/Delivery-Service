package com.opportunity.deliveryservice.search.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResultDto {

    private List<StoreSearchResultDto> stores;
    private int totalCount;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;

    public static SearchResultDto of(List<StoreSearchResultDto> stores) {
        return SearchResultDto.builder()
                .stores(stores)
                .totalCount(stores.size())
                .build();
    }

    public static SearchResultDto of(List<StoreSearchResultDto> stores,
                                     long totalElements,
                                     int totalPages,
                                     int currentPage,
                                     int pageSize) {
        return SearchResultDto.builder()
                .stores(stores)
                .totalCount(stores.size())
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build();
    }
}
