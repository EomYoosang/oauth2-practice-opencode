package com.eomyoosang.oauth2.common.exception;

import com.eomyoosang.oauth2.common.response.ApiErrorResponse;
import com.eomyoosang.oauth2.support.security.InvalidPasswordException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                                                               HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "%s: %s".formatted(error.getField(), error.getDefaultMessage()))
                .findFirst()
                .orElse(ErrorCode.VALIDATION_FAILED.getDefaultMessage());
        ApiErrorResponse body = ApiErrorResponse.of(ErrorCode.VALIDATION_FAILED, message, request.getRequestURI());
        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus()).body(body);
    }

    @ExceptionHandler(BindException.class)
    ResponseEntity<ApiErrorResponse> handleBindException(BindException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "%s: %s".formatted(error.getField(), error.getDefaultMessage()))
                .findFirst()
                .orElse(ErrorCode.VALIDATION_FAILED.getDefaultMessage());
        ApiErrorResponse body = ApiErrorResponse.of(ErrorCode.VALIDATION_FAILED, message, request.getRequestURI());
        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus()).body(body);
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiErrorResponse body = ApiErrorResponse.of(errorCode, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    ResponseEntity<ApiErrorResponse> handleInvalidPassword(InvalidPasswordException ex, HttpServletRequest request) {
        String message = ex.getViolations().isEmpty()
                ? ErrorCode.PASSWORD_POLICY_VIOLATION.getDefaultMessage()
                : String.join(", ", ex.getViolations());
        ApiErrorResponse body = ApiErrorResponse.of(ErrorCode.PASSWORD_POLICY_VIOLATION, message, request.getRequestURI());
        return ResponseEntity.status(ErrorCode.PASSWORD_POLICY_VIOLATION.getStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.getDefaultMessage(),
                request.getRequestURI());
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()).body(body);
    }
}
