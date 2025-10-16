package com.opportunity.deliveryservice.user.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opportunity.deliveryservice.user.domain.entity.Address;

// deletedAt IS NULL 인 데이터만 조회됨
public interface AddressRepository extends JpaRepository<Address, UUID> {
}