package com.dev.realtimechat.shared.global.api;

import lombok.Builder;
import lombok.Getter;

/**
 * 실패(에러발생)시 응답하는 클래스
 */
@Getter
public class ErrorResponse {
    private String message;
    private Throwable cause;

    @Builder
    public ErrorResponse(String message, Throwable cause) {
        this.message = message;
        this.cause = cause;
    }

    public static ErrorResponse of (String message, Throwable cause) {
        return ErrorResponse.builder()
                .message(message)
                .cause(cause)
                .build();
    }

    public static ErrorResponse of (String message) {
        return ErrorResponse.builder()
                .message(message)
                .cause(null)
                .build();
    }
}
