package com.opportunity.deliveryservice.cart.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_cart")
public class Cart {

	@Id
	@GeneratedValue
	private UUID id;

	// user와 연관관계 매핑 필요
	private Long userId;

	@OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartProducts> cartProducts = new ArrayList<>();

	public Cart(Long userId) {
		this.userId = userId;
	}

	// 연관관계 설정
	public void addCartProducts(CartProducts cartProducts) {
		this.cartProducts.add(cartProducts);
		cartProducts.setCart(this);
	}
}
