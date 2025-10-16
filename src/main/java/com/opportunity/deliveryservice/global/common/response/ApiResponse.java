package com.opportunity.deliveryservice.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"code", "message", "data"})
public class ApiResponse<T> {

	/**
	 * 커스텀 에러 코드. 성공 시 null
	 * 예: "OPPTY-CMN-403-001"
	 */
	private final String code;

	/**
	 * 사용자에게 보여줄 메시지. 주로 에러 발생 시 사용
	 */
	private final String message;

	/**
	 * 응답 본문 데이터. 성공 시 실제 결과 객체가 담김
	 */
	private final T data;

	/**
	 * 전체 생성자. 응답의 모든 필드를 지정하여 생성합니다.
	 */
	public ApiResponse(String code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	/**
	 * 성공 응답을 생성합니다.
	 */
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(null, null, data);
	}

	/**
	 * 성공 응답 (데이터 없이 메시지만)
	 */
	public static <T> ApiResponse<T> successNoData(String code, String message) {
		return new ApiResponse<>(code, message, null);
	}

	/**
	 * 본문 없이 성공 응답 (No Content)을 생성합니다.
	 */
	public static <T> ApiResponse<T> noContent() {
		return new ApiResponse<>(null, null, null);
	}

	/**
	 * 응답 데이터가 없는 실패 응답을 생성합니다.
	 */
	public static <T> ApiResponse<T> fail(String code, String message) {
		return new ApiResponse<>(code, message, null);
	}

	/**
	 * 실패 응답을 생성합니다.
	 */
	public static <T> ApiResponse<T> fail(String code, String message, T data) {
		return new ApiResponse<>(code, message, data);
	}

}

