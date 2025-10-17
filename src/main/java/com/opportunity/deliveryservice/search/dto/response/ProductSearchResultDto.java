package com.opportunity.deliveryservice.search.dto.response;

import com.opportunity.deliveryservice.product.domain.entity.Product;
import com.opportunity.deliveryservice.product.domain.entity.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchResultDto {

    private UUID id;
    private UUID storeId;
    private String title;
    private String description;
    private Long price;
    private ProductCategory category;
    private String image;
    private String type; // "PRODUCT"

    public static ProductSearchResultDto from(Product product) {
        return ProductSearchResultDto.builder()
                .id(product.getId())
                .storeId(product.getStoreId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .image(product.getImage())
                .type("PRODUCT")
                .build();
    }
}
