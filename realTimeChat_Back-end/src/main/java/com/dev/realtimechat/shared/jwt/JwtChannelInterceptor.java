package com.dev.realtimechat.shared.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

/**
 * WebSocket 연결을 처리하며, JWT 토큰 검증을 수행하는 인터셉터 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // CONNECT 요청의 경우
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 헤더에서 JWT 토큰 추출
            String ipAddress = accessor.getFirstNativeHeader("X-Client-IP");
            String token = accessor.getFirstNativeHeader("Authorization");
            String bojName = accessor.getFirstNativeHeader("bojName");

            // JWT 토큰 검증
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);

                try {
                    jwtProvider.validateToken(token, bojName, ipAddress);
                } catch (Exception e) {
                    log.error("Error during WS CONNECT (token validation): {}", e.getMessage());
                    return handleJwtException(e);
                }
            } else {
                log.warn("Authorization header is missing or invalid");
                return createErrorResponse("Authorization header is missing or invalid");
            }
        }
        log.info("검증 완료!");
        return message;
    }


    /**
     * 예외를 처리하고 적절한 에러 응답을 생성하는 메서드
     * @param e 발생한 예외
     * @return 에러 응답 메시지
     */
    private Message<?> handleJwtException(Exception e) {
        if (e instanceof ExpiredJwtException) {
            return createErrorResponse("Token expired");
        } else if (e instanceof BadCredentialsException) {
            return createErrorResponse("Invalid credentials");
        } else if (e instanceof SecurityException) {
            return createErrorResponse("Security issue");
        } else if (e instanceof MalformedJwtException) {
            return createErrorResponse("Malformed JWT");
        } else if (e instanceof UnsupportedJwtException) {
            return createErrorResponse("Unsupported JWT");
        } else if (e instanceof IllegalArgumentException) {
            return createErrorResponse("Invalid argument");
        }

        // 기본적으로 처리되지 않은 예외
        log.error("Unexpected error: {}", e.getMessage());
        return createErrorResponse("Authorization failed");
    }


    /**
     * 에러 메시지를 포함한 WebSocket 오류 메시지 생성
     * @param message 에러 메시지
     * @return 오류 메시지를 담은 Message
     */
    private Message<?> createErrorResponse(String message) {
        StompHeaderAccessor errorAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
        errorAccessor.setMessage(message);

        return MessageBuilder.createMessage(message, errorAccessor.getMessageHeaders());
    }
}
