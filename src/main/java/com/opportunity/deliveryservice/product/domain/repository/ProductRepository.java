package com.opportunity.deliveryservice.product.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.opportunity.deliveryservice.product.domain.entity.Product;

public interface ProductRepository extends JpaRepository<Product, UUID> {

	@Query("SELECT p FROM Product p WHERE p.title LIKE %:keyword% AND p.isVisible = true AND p.deletedAt IS NULL")
	List<Product> searchByTitle(@Param("keyword") String keyword);

	@Query("SELECT p FROM Product p WHERE p.title LIKE %:keyword% " +
			"AND p.store.id IN :storeIds " +
			"AND p.isVisible = true AND p.deletedAt IS NULL")
	List<Product> searchByTitleAndStoreIds(@Param("keyword") String keyword,
										   @Param("storeIds") List<UUID> storeIds);

	@Query("SELECT p FROM Product p WHERE p.title LIKE %:keyword% " +
			"AND (p.store.id NOT IN :excludeStoreIds) " +
			"AND p.isVisible = true AND p.deletedAt IS NULL")
	List<Product> searchByTitleExcludingStoreIds(@Param("keyword") String keyword,
												  @Param("excludeStoreIds") List<UUID> excludeStoreIds);

	@Query("SELECT p FROM Product p WHERE p.store.id = :storeId")
	List<Product> findAllByStoreId(@Param("storeId") UUID storeId);

	@Query("SELECT p FROM Product p WHERE p.store.id = :storeId AND p.deletedAt IS NULL")
	List<Product> findByStoreIdAndNotDeleted(@Param("storeId") UUID storeId);
}
