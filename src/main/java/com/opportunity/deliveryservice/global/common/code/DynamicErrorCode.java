package com.opportunity.deliveryservice.global.common.code;

import org.springframework.http.HttpStatus;

public class DynamicErrorCode implements BaseErrorCode {
    private final HttpStatus status;
    private final String code;
    private final String message;

    public DynamicErrorCode(int status, String code, String message) {
        this.status = HttpStatus.valueOf(status);
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}