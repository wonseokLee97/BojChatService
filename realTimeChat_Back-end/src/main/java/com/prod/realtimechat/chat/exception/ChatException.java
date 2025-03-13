package com.prod.realtimechat.chat.exception;

/**
 * HTTP 통신중 발생하는 예외 입니다.
 */
public class ChatException extends RuntimeException {
    public ChatException(String message) {
        super(message);
    }

    public static class UnauthorizedActionException extends CustomSocketException {
        static final String message = "본인만 수정/삭제 할 수 있습니다.";

        public UnauthorizedActionException() {
            super(message);
        }
    }
}
