package com.dev.realtimechat.shared.jwt;

public class JwtException extends RuntimeException {
    public JwtException(String message) {
        super(message);
    }

    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class EmptyTokenException extends JwtException {
        public EmptyTokenException() {
            super("Token is empty");
        }
    }

    public static class InvalidTokenException extends JwtException {
        public InvalidTokenException(String message) {
            super(message);
        }

        public InvalidTokenException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class InvalidBojNameException extends JwtException {
        public InvalidBojNameException(String message) {
            super(message);
        }

        public InvalidBojNameException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
