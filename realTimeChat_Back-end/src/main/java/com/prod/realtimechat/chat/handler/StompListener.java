package com.prod.realtimechat.chat.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.messaging.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class StompListener {
//    private final SubProtocolWebSocketHandler subProtocolWebSocketHandler;
    private final SimpMessagingTemplate messagingTemplate;
    private final ConcurrentHashMap<String, String> sessionProblemMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> problemUserCount = new ConcurrentHashMap<>();

    public StompListener(WebSocketHandler webSocketHandler, SimpMessagingTemplate messagingTemplate) {
//        this.subProtocolWebSocketHandler = (SubProtocolWebSocketHandler) webSocketHandler;
        this.messagingTemplate = messagingTemplate;
    }

    // CONNECT -> 구독 발생 이후..
    @EventListener(SessionSubscribeEvent.class)
    public void handleSessionSubscribed(SessionSubscribeEvent event) throws InterruptedException {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();
        log.info(destination);
        String problemId = extractProblemId(destination);

        // Entry 구독인 경우에만..
        if (problemId != null) {
            sessionProblemMap.put(sessionId, problemId);

            if (!problemUserCount.containsKey(problemId)) {
                problemUserCount.put(problemId, new AtomicInteger(0));
            }
            int entryCount = problemUserCount.get(problemId).incrementAndGet();

            Thread.sleep(500);
            messagingTemplate.convertAndSend("/sub/channel/entry" + problemId, entryCount);
        }
    }

    // 세션 종료 이후..
    @EventListener(SessionDisconnectEvent.class)
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        // sessionId 기반으로 problemId를 찾음
        String problemId = sessionProblemMap.remove(sessionId);

        if (problemId != null) {
            // 현재 problemId에 접속한 Entry 를 구함.
            AtomicInteger count = problemUserCount.get(problemId);
            int entryCount = count.decrementAndGet();

            // entryCount 가 0이하가 되면..
            if (entryCount <= 0) {
                // HashMap 에서 제거
                problemUserCount.remove(problemId);
            }

            messagingTemplate.convertAndSend("/sub/channel/entry" + problemId, entryCount);
        }
    }

    public String extractProblemId(String destination) {
        if (destination == null || !destination.startsWith("/sub/channel/entry")) {
            return null;
        }
        return destination.replace("/sub/channel/entry", "");
    }
}


