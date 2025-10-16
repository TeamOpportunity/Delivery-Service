package com.opportunity.deliveryservice.user.presentation.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import com.opportunity.deliveryservice.user.domain.entity.UserRoleEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminSearchUserRequestDto {

	private String searchType; // username, email 등
	private String keyword;
	private UserRoleEnum role;

	private int page = 1;
	private int size = 10;
	private String sortBy = "createdAt";
	private boolean isAsc = false;

	/**
	 * Pageable 객체 생성 및 페이지 크기 제한 규칙 적용
	 */
	public Pageable toPageable() {
		// 요구사항: 10건, 30건, 50건 외의 건수는 10건으로 고정
		int fixedSize = switch (size) {
			case 30 -> 30;
			case 50 -> 50;
			default -> 10;
		};

		String actualSortBy = StringUtils.hasText(sortBy) ? sortBy : "createdAt";

		Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
		Sort sort = Sort.by(direction, actualSortBy);

		// Pageable은 0부터 시작하므로 page - 1
		return PageRequest.of(page > 0 ? page - 1 : 0, fixedSize, sort);
	}
}
