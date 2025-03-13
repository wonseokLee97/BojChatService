package com.prod.realtimechat.shared.jwt;

import com.prod.realtimechat.shared.global.api.ApiResponse;
import com.prod.realtimechat.shared.global.type.http.HttpErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * SecurityFilter 내에서 발생한 오류만 Catch
 */

@RestControllerAdvice
@Slf4j
public class JwtExceptionHandler {

    @ExceptionHandler(JwtException.InvalidBojNameException.class)
    protected ApiResponse<?> handleInvalidBojNameException(JwtException.InvalidBojNameException e) {
        return ApiResponse
                .error(
                        e.getMessage(),
                        HttpErrorType.MISMATCH_NICKNAME,
                        e.getCause()
                );
    }

    @ExceptionHandler(JwtException.InvalidTokenException.class)
    protected ApiResponse<?> handleInvalidTokenException(JwtException.InvalidTokenException e) {
        return ApiResponse
                .error(
                        e.getMessage(),
                        HttpErrorType.INVALID_TOKEN,
                        e.getCause()
                );
    }
}
