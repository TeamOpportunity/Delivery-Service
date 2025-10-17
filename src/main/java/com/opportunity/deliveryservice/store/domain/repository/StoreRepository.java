package com.opportunity.deliveryservice.store.domain.repository;

import com.opportunity.deliveryservice.store.domain.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {

    @Query("SELECT s FROM Store s " +
            "LEFT JOIN FETCH s.storeCategories sc " +
            "LEFT JOIN FETCH sc.category " +
            "WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<Store> findByIdAndNotDeleted(@Param("id") UUID id);

    @Query("SELECT DISTINCT s FROM Store s " +
            "LEFT JOIN FETCH s.storeCategories sc " +
            "LEFT JOIN FETCH sc.category " +
            "WHERE s.userId = :userId AND s.deletedAt IS NULL")
    List<Store> findByUserIdAndNotDeleted(@Param("userId") Long userId);

    @Query("SELECT DISTINCT s FROM Store s " +
            "JOIN FETCH s.storeCategories sc " +
            "JOIN FETCH sc.category " +
            "WHERE sc.category.id = :categoryId AND s.deletedAt IS NULL")
    List<Store> findByCategoryIdAndNotDeleted(@Param("categoryId") Integer categoryId);

    @Query("SELECT DISTINCT s FROM Store s " +
            "LEFT JOIN FETCH s.storeCategories sc " +
            "LEFT JOIN FETCH sc.category " +
            "WHERE s.city = :city AND s.gu = :gu AND s.deletedAt IS NULL")
    List<Store> findByCityAndGuAndNotDeleted(@Param("city") String city, @Param("gu") String gu);

    @Query("SELECT DISTINCT s FROM Store s " +
            "LEFT JOIN FETCH s.storeCategories sc " +
            "LEFT JOIN FETCH sc.category " +
            "WHERE s.name LIKE %:name% AND s.deletedAt IS NULL")
    List<Store> findByNameContainingAndNotDeleted(@Param("name") String name);

    // 페이징 지원 - 검색용
    @Query("SELECT s FROM Store s WHERE s.name LIKE %:name% AND s.deletedAt IS NULL")
    Page<Store> findByNameContainingAndNotDeleted(@Param("name") String name, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Store s " +
            "LEFT JOIN FETCH s.storeCategories sc " +
            "LEFT JOIN FETCH sc.category " +
            "WHERE s.id IN :ids AND s.deletedAt IS NULL")
    List<Store> findAllByIdAndNotDeleted(@Param("ids") List<UUID> ids);

    boolean existsByIdAndUserId(UUID id, Long userId);
}
