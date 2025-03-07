//package com.dev.realtimechat.chat.handler;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.AbstractWebSocketHandler;
//
//@Component
//@Slf4j
//@Primary
//public class CustomWebSocketHandler extends AbstractWebSocketHandler {
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) {
//        log.info("WebSocket 연결 성공: {}", session.getId());
//    }
//
//    @Override
//    public void handleTransportError(WebSocketSession session, Throwable exception) {
//        log.error("WebSocket 연결 오류: {}", exception.getMessage());
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
//        log.info("WebSocket 연결 종료: {}, 상태: {}", session.getId(), status);
//    }
//}
//
