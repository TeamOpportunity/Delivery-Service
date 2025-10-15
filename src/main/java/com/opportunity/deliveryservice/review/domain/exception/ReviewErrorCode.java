package com.opportunity.deliveryservice.review.domain.exception;

import org.springframework.http.HttpStatus;

import com.opportunity.deliveryservice.global.common.code.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements BaseErrorCode {

	// 리뷰 관련
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "OPPTY-RVW-404-01", "존재하지 않는 리뷰입니다."),
	DUPLICATE_REVIEW(HttpStatus.CONFLICT, "OPPTY-RVW-409-01", "이미 리뷰가 존재합니다."),
	INVALID_REVIEW_OWNER(HttpStatus.FORBIDDEN, "OPPTY-RVW-403-01", "리뷰 작성자가 아닙니다."),
	INVALID_RATING(HttpStatus.BAD_REQUEST, "OPPTY-RVW-400-01", "별점은 1~5 사이여야 합니다."),

	// 답글 관련
	REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "OPPTY-RPL-404-01", "존재하지 않는 답글입니다."),
	DUPLICATE_REPLY(HttpStatus.CONFLICT, "OPPTY-RPL-409-01", "이미 답글이 존재합니다."),
	INVALID_REPLY_OWNER(HttpStatus.FORBIDDEN, "OPPTY-RPL-403-01", "답글 작성자가 아닙니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}