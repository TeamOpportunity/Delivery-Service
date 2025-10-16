package com.opportunity.deliveryservice.user.domain.entity;

public enum UserRoleEnum {
	MASTER(Authority.MASTER), // 최종 관리자
	MANAGER(Authority.MANAGER), // 중간 관리자
	OWNER(Authority.OWNER), // 가게 사장
	CUSTOMER(Authority.CUSTOMER); // 고객

	private final String authority;

	UserRoleEnum(String authority) {
		this.authority = authority;
	}

	public String getAuthority() {
		return this.authority;
	}

	public static class Authority {
		public static final String MASTER = "ROLE_MASTER";
		public static final String MANAGER = "ROLE_MANAGER";
		public static final String OWNER = "ROLE_OWNER";
		public static final String CUSTOMER = "ROLE_CUSTOMER";
	}
}

