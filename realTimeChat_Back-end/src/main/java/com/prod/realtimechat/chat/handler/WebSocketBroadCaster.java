//package com.dev.realtimechat.infra.handler;
//
//import com.dev.realtimechat.Chat.application.ChatService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.PongMessage;
//
//@Component
//@RequiredArgsConstructor
//@EnableScheduling
//@Slf4j
//public class WebSocketBroadCaster {
//    private final SimpMessagingTemplate messagingTemplate;
//    private final ChatService chatService;
//    private @Value("${server.chat-room-id}") String problemId;
//
//    // 25초마다 참여 인원 수를 브로드캐스트
//    @Scheduled(fixedRate = 5000) // 25초 간격
//    public void broadcastSessionCount() {
//        int entryCount = chatService.getEntry(problemId);
//
//        // 클라이언트에게 전송
//        messagingTemplate.convertAndSend("/sub/channel/entry" + problemId, entryCount);
//    }
//}
