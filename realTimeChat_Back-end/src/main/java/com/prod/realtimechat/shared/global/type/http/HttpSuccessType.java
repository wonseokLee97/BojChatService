package com.dev.realtimechat.shared.global.type.http;

import com.dev.realtimechat.shared.global.type.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HttpSuccessType implements Type {

    // 200: 요청 성공
    SUCCESS_GET_USER(200, "유저 정보 조회 성공."),
    SUCCESS_GET_ENTRY_COUNT(200, "참여자 조회 성공"),
    SUCCESS_GET_CHAT_LIST(200, "채팅 목록 조회 성공"),
    SUCCESS_SIGN_UP(200, "회원가입 성공."),
    SUCCESS_SIGN_IN(200, "로그인 성공"),
    SUCCESS_LOGOUT(200, "로그아웃 성공"),
    SUCCESS_ROOM_CREATE(201,"채팅방 생성 성공"),
    SUCCESS_TOKEN_ISSUANCE(201, "토큰 발급 성공");

    private final int statusCode;
    private final String message;
}
