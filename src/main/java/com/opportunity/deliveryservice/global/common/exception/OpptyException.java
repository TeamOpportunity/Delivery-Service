package com.opportunity.deliveryservice.global.common.exception;

import com.opportunity.deliveryservice.global.common.code.BaseErrorCode;

import lombok.Getter;

@Getter
public class OpptyException extends RuntimeException {

	private final BaseErrorCode errorCode;

	public OpptyException(BaseErrorCode errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public OpptyException(BaseErrorCode errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}
}
