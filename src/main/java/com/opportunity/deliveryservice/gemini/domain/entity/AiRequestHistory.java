package com.opportunity.deliveryservice.gemini.domain.entity;

import java.util.UUID;

import com.opportunity.deliveryservice.global.common.entity.BaseEntity;
import com.opportunity.deliveryservice.product.domain.entity.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "p_ai_request_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiRequestHistory extends BaseEntity {

	@GeneratedValue
	@Id
	UUID id;

	String requestPrompt;
	String response;

	@OneToOne
	Product product;

	@Builder
	public AiRequestHistory(String requestPrompt, String response, Product product){
		this.requestPrompt = requestPrompt;
		this.response = response;
		this.product = product;
	}
}
