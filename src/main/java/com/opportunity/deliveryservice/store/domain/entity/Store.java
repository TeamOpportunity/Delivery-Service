package com.opportunity.deliveryservice.store.domain.entity;

import com.opportunity.deliveryservice.global.common.entity.BaseEntity;
import com.opportunity.deliveryservice.review.domain.entity.Review;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_store")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Store extends BaseEntity {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false, length = 36)
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<StoreCategory> storeCategories = new ArrayList<>();

	@Column(name = "city", nullable = false, length = 100)
	private String city;

	@Column(name = "gu", nullable = false, length = 100)
	private String gu;

	@Column(name = "detail_address", nullable = false, length = 100)
	private String detailAddress;

	@Column(name = "content", columnDefinition = "TEXT")
	private String content;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "min_order_price", nullable = false)
	private int minOrderPrice;

	@Column(name = "start_time", nullable = false)
	private LocalTime startTime;

	@Column(name = "end_time", nullable = false)
	private LocalTime endTime;

	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Review> reviews = new ArrayList<>();

	public void update(String city, String gu, String detailAddress,
		String content, String name, int minOrderPrice,
		LocalTime startTime, LocalTime endTime) {
		this.city = city;
		this.gu = gu;
		this.detailAddress = detailAddress;
		this.content = content;
		this.name = name;
		this.minOrderPrice = minOrderPrice;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public void addCategory(Category category) {
		StoreCategory storeCategory = StoreCategory.of(this, category);
		this.storeCategories.add(storeCategory);
	}

	public void updateCategories(List<Category> categories) {
		this.storeCategories.clear();
		categories.forEach(this::addCategory);
	}

	public void addReview(Review review) {
		this.reviews.add(review);
		if (review.getStore() != this) {
			review.setStore(this);
		}
	}

	public void delete(Long deletedBy) {
		super.softDelete(deletedBy);
	}
}
