package com.dev.realtimechat.shared.global.api;

import com.dev.realtimechat.shared.global.type.Type;
import lombok.Builder;
import lombok.Data;

/**
 * 모든 요청에 공통으로 보낼 responseDto
 */

@Data
public class ApiResponse<T> {
    private boolean isSuccess;
    private T response;
    private ErrorResponse error;
    private Type type;

    // 제네릭 타입의 response 에는 DTO, SuccessResponse 등이 들어갈 수 있다.
    @Builder
    public ApiResponse(boolean isSuccess, T response, ErrorResponse error, Type type) {
        this.isSuccess = isSuccess;
        this.response = response;
        this.error = error;
        this.type = type;
    }

    public static <T> ApiResponse<T> success(T response, Type type) {
        return ApiResponse.<T>builder()
                .isSuccess(true)
                .response(response)
                .error(null)
                .type(type)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, Type type) {
        return ApiResponse.<T>builder()
                .isSuccess(false)
                .response(null)
                .error(ErrorResponse.of(message))
                .type(type)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, Type type, Throwable cause) {
        return ApiResponse.<T>builder()
                .isSuccess(false)
                .response(null)
                .error(ErrorResponse.of(message, cause))
                .type(type)
                .build();
    }

    public static <T> ApiResponse<T> error(T response, String message, Type type) {
        return ApiResponse.<T>builder()
                .isSuccess(false)
                .response(response)
                .error(ErrorResponse.of(message))
                .type(type)
                .build();
    }

    public static <T> ApiResponse<T> error(T response, String message, Type type, Throwable cause) {
        return ApiResponse.<T>builder()
                .isSuccess(false)
                .response(response)
                .error(ErrorResponse.of(message, cause))
                .type(type)
                .build();
    }
}