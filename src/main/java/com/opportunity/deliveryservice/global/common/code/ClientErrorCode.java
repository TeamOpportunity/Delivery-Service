package com.opportunity.deliveryservice.global.common.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 클라이언트 요청에 대한 에러 코드를 정의합니다.
 *
 *  형식: OPPTY-[도메인코드]-[HTTP 상태코드]-[에러번호]
 * 	도메인코드
 * 	HTTP 상태코드 (3자리)
 *	에러번호 (3자리): 도메인 내 개별 에러 식별 번호
 *
 * <p>
 */
@Getter
@RequiredArgsConstructor
public enum ClientErrorCode implements BaseErrorCode {

	// 400 Bad Request
	INVALID_RATING(HttpStatus.BAD_REQUEST, "OPPTY-RVW-400-01", "별점은 1~5 사이여야 합니다."),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "OPPTY-CMN-400-01", "입력값이 올바르지 않습니다."),
	INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "OPPTY-CMN-400-02", "JSON 형식이 유효하지 않습니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "OPPTY-CMN-405-01", "허용되지 않은 HTTP 메서드입니다."),
	INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "OPPTY-PAY-400-01", "요청 금액과 승인 금액이 다릅니다."),

	// 401 Unauthorized
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "OPPTY-CMN-401-01", "인증이 필요한 요청입니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "OPPTY-CMN-401-02", "유효하지 않은 인증 토큰입니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "OPPTY-CMN-401-03", "만료된 Access Token 토큰입니다. 토큰 재발급이 필요합니다."),
	BLACKLISTED_TOKEN(HttpStatus.UNAUTHORIZED, "OPPTY-CMN-401-04", "무효화된 토큰입니다. 다시 로그인 해주세요."), // 로그아웃, 재발급된 토큰

	// 403 Forbidden
	INVALID_REPLY_OWNER(HttpStatus.FORBIDDEN, "OPPTY-RPL-403-01", "답글 작성자가 아닙니다."),
	INVALID_ORDER_USER(HttpStatus.FORBIDDEN, "OPPTY-RPL-403-01", "리뷰 작성 대상이 아닙니다."),
	INVALID_REVIEW_OWNER(HttpStatus.FORBIDDEN, "OPPTY-RVW-403-01", "리뷰 작성자가 아닙니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "OPPTY-CMN-403-01", "해당 요청에 대한 접근 권한이 없습니다."),
	UNAUTHORIZED_ROLE_CHANGE(HttpStatus.FORBIDDEN, "OPPTY-USR-403-01", "권한을 변경할 수 있는 권한이 없습니다."),

	// 404 Not Found
	STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "OPPTY-STR-404-01", "가게를 찾을 수 없습니다."),
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "OPPTY-STR-404-01", "주문을 찾을 수 없습니다."),
	REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "OPPTY-RPL-404-01", "존재하지 않는 답글입니다."),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "OPPTY-CMN-404-01", "요청한 리소스를 찾을 수 없습니다."),
	ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "OPPTY-ADD-404-01", "주소록을 찾을 수 없습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "OPPTY-USR-404-01", "사용자를 찾을 수 없습니다."),
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "OPPTY-RVW-404-01", "존재하지 않는 리뷰입니다."),

	// 409 Conflict
	DUPLICATE_REPLY(HttpStatus.CONFLICT, "OPPTY-RPL-409-01", "이미 답글이 존재합니다."),
	DUPLICATE_REVIEW(HttpStatus.CONFLICT, "OPPTY-RVW-409-01", "이미 리뷰가 존재합니다."),
	CONFLICT(HttpStatus.CONFLICT, "OPPTY-CMN-409-01", "요청이 서버의 현재 상태와 충돌합니다."),
	DUPLICATE_USERNAME(HttpStatus.CONFLICT, "OPPTY-USR-409-01", "이미 존재하는 아이디입니다."),
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, "OPPTY-USR-409-02", "이미 존재하는 이메일입니다."),
	INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "OPPTY-USR-409-03", "아이디 또는 비밀번호가 일치하지 않습니다."),
	INVALID_ADMIN_KEY(HttpStatus.FORBIDDEN, "OPPTY-USR-409-04", "유효하지 않은 관리자 인증 키입니다."),
	ORDER_ALREADY_CONFIRMED(HttpStatus.CONFLICT, "OPPTY-ORD-409-01", "이미 주문이 허락되어 준비중입니다."),
	ORDER_ALREADY_REVIEWED(HttpStatus.CONFLICT, "OPPTY-ORD-409-01", "이미 주문이 허락되어 준비중입니다."),
	CANCELLATION_TIME_EXPIRED(HttpStatus.CONFLICT, "OPPTY-ORD-409-02", "주문 취소 가능한 시간이 지났습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
