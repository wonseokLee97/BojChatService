package com.dev.realtimechat.shared.global.type.http;

import com.dev.realtimechat.shared.global.type.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum HttpErrorType implements Type {

    // 400 BAD_REQUEST: 잘못된 요청
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 파라미터"),
    ALREADY_CREATED_TOKEN(HttpStatus.BAD_REQUEST, "이미 존재하는 토큰"),

    // 401 UNAUTHORIZED: 인증 실패
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰"),
    NO_TOKEN(HttpStatus.UNAUTHORIZED, "존재하지 않는 토큰"),

    // 403 FORBIDDEN: 권한 없음
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "권한 없음"),
    MISMATCH_NICKNAME(HttpStatus.FORBIDDEN, "토큰 발급자와 닉네임 불일치"),

    // 404 NOT_FOUND: 리소스를 찾을 수 없음
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 유저"),
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "존재하지 않는 토큰"),
    NOT_FOUND_CHATROOM(HttpStatus.NOT_FOUND, "존재하지 않는 채팅방"),

    // 409 CONFLICT: 충돌 발생
    ALREADY_CREATED_USER_ID(HttpStatus.CONFLICT, "중복된 유저 아이디"),
    ALREADY_CREATED_EMAIL(HttpStatus.CONFLICT, "중복된 이메일"),

    // 500 INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류");

    private final HttpStatus status;
    private final String message;
}

