package com.opportunity.deliveryservice.gemini.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opportunity.deliveryservice.gemini.domain.entity.AiRequestHistory;

public interface AiRequestHistoryRepository extends JpaRepository<AiRequestHistory, UUID> {
}
