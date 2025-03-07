package com.dev.realtimechat.shared.global.type.ws;

import com.dev.realtimechat.shared.global.type.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WsErrorType implements Type {

    // 1000~1999: 정상 종료 관련
    NORMAL_CLOSURE(1000, "정상적으로 WebSocket 연결이 종료됨"),

    // 2000~2999: 비정상 종료 관련
    GOING_AWAY(1001, "서버 또는 클라이언트가 연결을 종료함"),
    PROTOCOL_ERROR(1002, "WebSocket 프로토콜 위반"),
    UNSUPPORTED_DATA(1003, "서버가 지원하지 않는 데이터 형식"),
    NO_STATUS_RECEIVED(1005, "클라이언트가 응답 없이 종료됨"),
    ABNORMAL_CLOSURE(1006, "네트워크 문제 등으로 인한 비정상 종료"),
    INVALID_FRAME_PAYLOAD_DATA(1007, "잘못된 형식의 데이터가 전송됨"),
    POLICY_VIOLATION(1008, "서버 정책 위반"),
    MESSAGE_TOO_BIG(1009, "전송된 메시지가 너무 큼"),
    MISSING_EXTENSION(1010, "필요한 WebSocket 확장이 지원되지 않음"),
    INTERNAL_ERROR(1011, "서버 내부 오류 발생"),

    // 3000~: 커스텀 에러 (애플리케이션 정의)
    SOCKET_ERROR(3000, "소켓 내부 오류"),
    CONNECTION_TIMEOUT(3001, "연결 시간이 초과됨"),
    UNAUTHORIZED_ACCESS(3002, "인증 실패"),
    FORBIDDEN_ACTION(3003, "허용되지 않은 WebSocket 요청"),
    INVALID_MESSAGE_FORMAT(3004, "잘못된 메시지 포맷"),
    SERVER_OVERLOAD(3005, "서버 과부하로 인해 요청 처리 불가");

    private final int code;
    private final String message;
}

