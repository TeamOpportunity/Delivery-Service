package com.opportunity.deliveryservice.store.domain.repository;

import com.opportunity.deliveryservice.store.domain.entity.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, UUID> {
}
