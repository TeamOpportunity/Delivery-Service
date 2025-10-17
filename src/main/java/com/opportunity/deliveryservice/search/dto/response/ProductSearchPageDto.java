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
public class ProductSearchPageDto {

    private List<ProductSearchResultDto> products;
    private int totalCount;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;

    public static ProductSearchPageDto of(List<ProductSearchResultDto> products,
                                          long totalElements,
                                          int totalPages,
                                          int currentPage,
                                          int pageSize) {
        return ProductSearchPageDto.builder()
                .products(products)
                .totalCount(products.size())
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build();
    }
}
