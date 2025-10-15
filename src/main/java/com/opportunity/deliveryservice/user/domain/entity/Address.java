package com.opportunity.deliveryservice.user.domain.entity;

import java.util.UUID;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import com.opportunity.deliveryservice.user.presentation.dto.request.AddressRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 필터 조건: isDeleted가 true일 경우 'deleted_at IS NULL' 조건을 무시함
// @Filter(name = "softDeleteFilter", condition = "deleted_at IS NULL OR  :isDeleted = true")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_user_address")
// 삭제 처리 하지않고 update(soft delete) 처리
@SQLDelete(sql = "update p_user_address set deleted_at = NOW() where id = ?")
@FilterDef(name = "deletedAddressFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "deletedAddressFilter", condition = "deleted_at IS NULL OR :isDeleted = true")

public class Address extends TimeStamped {

	@Id // 기본키 & 자동생성: UUID
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column
	private String city;

	@Column
	private String gu;

	@Column(name = "detail_address")
	private String detailAddress;

	@Column
	private String nickname;

	@ManyToOne(fetch = FetchType.LAZY) // 외래키
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public Address(AddressRequestDto requestDto, User user) {
		this.city = requestDto.getCity();
		this.gu = requestDto.getGu();
		this.detailAddress = requestDto.getDetailAddress();
		this.nickname = requestDto.getNickname();
		this.user = user;
	}

	// 주소록 수정
	public void update(AddressRequestDto requestDto) {
		this.city = requestDto.getCity();
		this.gu = requestDto.getGu();
		this.detailAddress = requestDto.getDetailAddress();
		this.nickname = requestDto.getNickname();
	}
}
