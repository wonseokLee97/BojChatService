package com.prod.realtimechat.chat.exception;

/**
 * WS 통신중 발생하는 예외 입니다.
 */
// Unchecked Exception
public class CustomSocketException extends RuntimeException{

    public CustomSocketException(String message) {
        super(message);
    }

    public CustomSocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class NoSuchChatRoomExceptionCustom extends CustomSocketException {
        static final String message = "채팅방을 찾을 수 없습니다.";

        public NoSuchChatRoomExceptionCustom() {
            super(message);
        }
    }

    // WebSocket 메시지 길이 초과 예외
    public static class MessageLengthExceededException extends CustomSocketException {
        static final String message = "메시지 길이가 150자를 초과했습니다.";

        public MessageLengthExceededException() {
            super(message);
        }
    }
}
