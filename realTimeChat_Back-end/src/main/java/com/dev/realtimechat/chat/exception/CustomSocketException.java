package com.dev.realtimechat.chat.exception;

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
}
