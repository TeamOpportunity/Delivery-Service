package com.opportunity.deliveryservice.global.common.exception.handler;

import org.aspectj.bridge.MessageUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.opportunity.deliveryservice.global.common.code.BaseErrorCode;
import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.code.ServerErrorCode;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.global.common.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(OpptyException.class)
	public ResponseEntity<ApiResponse<Void>> handleOpptyException(OpptyException ex) {
		BaseErrorCode errorCode = ex.getErrorCode();
		log.warn("OpptyException occurred: code={}, message={}", errorCode.getCode(), errorCode.getMessage(), ex);
		return buildErrorResponse(errorCode);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGeneralError(Exception ex) {
		log.error("Unexpected error occurred", ex);
		ServerErrorCode errorCode = ServerErrorCode.INTERNAL_SERVER_ERROR;
		return buildErrorResponse(errorCode);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
		ClientErrorCode errorCode = ClientErrorCode.INVALID_INPUT_VALUE;

		String validationMessage = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.findFirst()
			.map(FieldError::getDefaultMessage)
			.filter(StringUtils::hasText)
			.orElse(errorCode.getMessage());

		return ResponseEntity.badRequest().body(ApiResponse.fail(errorCode.getCode(), validationMessage));
	}

	private ResponseEntity<ApiResponse<Void>> buildErrorResponse(BaseErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ApiResponse.fail(errorCode.getCode(), errorCode.getMessage()));
	}

	private ResponseEntity<ApiResponse<Void>> buildErrorResponse(BaseErrorCode errorCode, String customMessage) {
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ApiResponse.fail(errorCode.getCode(), customMessage));
	}
}
