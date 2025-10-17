package com.opportunity.deliveryservice.user.domain.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import com.opportunity.deliveryservice.global.common.entity.BaseEntity;
import com.opportunity.deliveryservice.review.domain.entity.Review;
import com.opportunity.deliveryservice.user.presentation.dto.request.UserSignupRequestDto;
import com.opportunity.deliveryservice.user.presentation.dto.request.UserUpdateRequestDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 수준을 PROTECTED로 설정
// SQL Delete문을 실행하지않고(삭제 처리 하지않고) Update(soft delete)문 실행

@SQLDelete(sql = "UPDATE p_users SET deleted_at = NOW() WHERE id = ?")
@FilterDef(name = "deletedUserFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "deletedUserFilter", condition = "deleted_at IS NULL OR :isDeleted = true")

// 필터 정의: "softDeleteFilter"라는 이름과 isDeleted 매개변수 정의
@Table(name = "p_users")
@Entity
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private UserRoleEnum role;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@Filter(name = "deletedAddressFilter", condition = "deleted_at IS NULL OR :isDeleted = true")
	@BatchSize(size = 100)
	private List<Address> addressList = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Review> reviews = new ArrayList<>();

	public User(UserSignupRequestDto requestDto, String encodedPassword) {
		this.username = requestDto.getUsername();
		this.email = requestDto.getEmail();
		this.password = encodedPassword;
		this.role = requestDto.getRole();
	}

	// 회원 정보 수정
	public void update(UserUpdateRequestDto requestDto, String encodedPassword) {
		if (requestDto.getEmail() != null) {
			this.email = requestDto.getEmail();
		}
		if (encodedPassword != null) {
			this.password = encodedPassword;
		}
	}

	// 관리자(MASTER)에 의한 권한 변경
	public void updateRole(UserRoleEnum role) {
		this.role = role;
	}
}
