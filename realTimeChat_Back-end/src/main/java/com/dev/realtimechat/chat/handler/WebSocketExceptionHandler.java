package com.dev.realtimechat.chat.handler;

import com.dev.realtimechat.shared.global.api.ApiResponse;
import com.dev.realtimechat.shared.global.type.http.HttpErrorType;
import com.dev.realtimechat.shared.global.type.ws.WsErrorType;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.SocketException;

/**
 * 웹 소켓에서 발생한 오류만 Catch
 */
@RestControllerAdvice
@Slf4j
public class WebSocketExceptionHandler {

    @MessageExceptionHandler(SocketException.class)
    @SendToUser(destinations = "/queue/errors", broadcast = false)  // 해당 Session 의 User 에게만 예외 메시지 전송
    protected ApiResponse<?> handleSocketException(SocketException e) {
        return ApiResponse
                .error(
                        e.getMessage(),
                        WsErrorType.SOCKET_ERROR,
                        e.getCause()
                );
    }

    @MessageExceptionHandler(BadCredentialsException.class)
    @SendToUser(destinations = "/queue/errors", broadcast = false)  // 해당 Session 의 User 에게만 예외 메시지 전송
    protected ApiResponse<?> handleInvalidToken(BadCredentialsException e) {
        log.error("BadCredentialsException");
        return ApiResponse
                .error(
                        e.getMessage(),
                        HttpErrorType.INVALID_TOKEN,
                        e.getCause()
                );
    }

    @MessageExceptionHandler(ExpiredJwtException.class)
    @SendToUser(destinations = "/queue/errors", broadcast = false)  // 해당 Session 의 User 에게만 예외 메시지 전송
    protected ApiResponse<?> handleExpiredToken(ExpiredJwtException e) {
        log.error("ExpiredJwtException");
        return ApiResponse
                .error(
                        e.getMessage(),
                        HttpErrorType.EXPIRED_TOKEN,
                        e.getCause()
                );
    }
}
