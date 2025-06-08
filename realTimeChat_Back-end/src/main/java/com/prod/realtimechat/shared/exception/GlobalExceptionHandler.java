package com.prod.realtimechat.shared.exception;

import com.prod.realtimechat.presentation.exception.ChatException;
import com.prod.realtimechat.shared.api.ApiResponse;
import com.prod.realtimechat.shared.type.http.HttpErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Controller, Service Layer 에서 발생한 오류만 Catch
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ChatException.UnauthorizedActionException.class)
    public ApiResponse<?> handleUnauthorizedActionException(ChatException.UnauthorizedActionException e) {
        // 적절한 메시지와 함께 ApiResponse 반환
        return ApiResponse.error(
                e.getMessage(),
                HttpErrorType.FORBIDDEN_ACCESS);
    }

}
