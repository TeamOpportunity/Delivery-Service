package com.opportunity.deliveryservice.review.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opportunity.deliveryservice.review.domain.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, UUID> {

}
