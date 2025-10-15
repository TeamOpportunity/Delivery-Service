package com.opportunity.deliveryservice.user.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.user.domain.entity.Address;
import com.opportunity.deliveryservice.user.domain.entity.User;
import com.opportunity.deliveryservice.user.domain.entity.UserRoleEnum;
import com.opportunity.deliveryservice.user.domain.repository.AddressRepository;
import com.opportunity.deliveryservice.user.presentation.dto.request.AddressRequestDto;
import com.opportunity.deliveryservice.user.presentation.dto.response.AddressResponseDto;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceV1 {

	private final AddressRepository addressRepository;
	private final EntityManager entityManager;

	/**
	 * 주소록 생성
	 */
	@Transactional
	public AddressResponseDto createAddress(AddressRequestDto requestDto, User user) {
		Address address = new Address(requestDto, user);

		address = addressRepository.save(address);
		return new AddressResponseDto(address);
	}

	/**
	 * 주소록 수정
	 */
	@Transactional
	public AddressResponseDto updateAddress(UUID addressId, AddressRequestDto requestDto, User currentUser) {
		Address address = getAddressById(addressId);

		// 권한 검증: 소유자 또는 MASTER 수정 가능
		checkAuthority(address, currentUser);

		address.update(requestDto);
		return new AddressResponseDto(address);
	}

	/**
	 * 주소록 삭제 (Soft Delete)
	 */
	@Transactional
	public void deleteAddress(UUID addressId, User currentUser) {
		Address address = getAddressById(addressId);

		// 권한 검증: 소유자 또는 MASTER/MANAGER만 삭제 가능
		checkAuthority(address, currentUser);

		if (address.getDeletedAt() == null) {
			// Soft Delete 실행 (@SQLDelete에 의해 update 쿼리 실행)
			addressRepository.delete(address);
		} else {
			throw new OpptyException(ClientErrorCode.RESOURCE_NOT_FOUND);
		}
	}

	/**
	 * 주소록 엔티티 조회
	 */
	private Address getAddressById(UUID addressId) {
		// @SQLRestriction에 의해 deletedAt IS NULL 인 데이터만 조회됨
		return addressRepository.findById(addressId).orElseThrow(() ->
			new OpptyException(ClientErrorCode.ADDRESS_NOT_FOUND)
		);
	}

	/**
	 * 주소록 수정/삭제 권한 검증 로직
	 */
	private void checkAuthority(Address address, User currentUser) {
		User targetUser = address.getUser(); // 주소록 소유자

		// 1. 소유자(Owner) 검증: 본인이라면 권한에 관계없이 즉시 통과
		if (targetUser.getId().equals(currentUser.getId())) {
			return;
		}

		// 2. MASTER 권한 검증: 현재 사용자가 MASTER라면 타인의 주소록이라도 통과
		if (currentUser.getRole() == UserRoleEnum.MASTER) {
			return;
		}

		// 3. 위의 두 조건을 통과하지 못한 경우 (MANAGER, CUSTOMER 등이 타인의 주소록에 접근)
		throw new OpptyException(ClientErrorCode.FORBIDDEN);
	}
}