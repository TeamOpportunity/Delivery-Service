package com.opportunity.deliveryservice.global.common.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 서버 요청에 대한 에러 코드를 정의합니다.
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
public enum ServerErrorCode implements BaseErrorCode {

	// 500 Internal Server Error
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OPPTY-CMN-500-01", "서버 내부 오류가 발생했습니다. 관리자에게 문의해주세요."),
	DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OPPTY-CMN-500-02", "데이터베이스 처리 중 오류가 발생했습니다."),

	// 503 Service Unavailable
	SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "OPPTY-CMN-503-01", "현재 서비스를 이용할 수 없습니다. 잠시 후 다시 시도해주세요.");


	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
