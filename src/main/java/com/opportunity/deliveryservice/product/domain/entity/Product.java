package com.opportunity.deliveryservice.product.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLDelete;

import com.opportunity.deliveryservice.global.common.entity.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_product")
public class Product extends BaseEntity {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false)
	private Long price;

	@Column(nullable = false)
	private String title;

	@Column(nullable = true)
	private String description;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ProductCategory category;

	@Column(nullable = true, columnDefinition = "TEXT")
	private String image;

	@Column(nullable = false)
	private boolean isVisible = true;

	// @ManyToOne
	// private Store store


	@Builder
	public Product(String title, String description, Long price, ProductCategory category, String image){
		this.title = title;
		this.description = description;
		this.price = price;
		this.category = category;
		this.image = image;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void changeVisible(){
		this.isVisible = !this.isVisible;
	}

	public void updateProduct(String title, String description, Long price, ProductCategory category, String image) {
		Optional.ofNullable(title).ifPresent(value -> this.title = value);
		Optional.ofNullable(description).ifPresent(value -> this.description = value);
		Optional.ofNullable(price).ifPresent(value -> this.price = value);
		Optional.ofNullable(category).ifPresent(value -> this.category = value);
		Optional.ofNullable(image).ifPresent(value -> this.image = value);
	}
}
