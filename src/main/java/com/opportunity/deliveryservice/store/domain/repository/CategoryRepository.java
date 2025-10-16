package com.opportunity.deliveryservice.store.domain.repository;

import com.opportunity.deliveryservice.store.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByCategory(String category);
}
