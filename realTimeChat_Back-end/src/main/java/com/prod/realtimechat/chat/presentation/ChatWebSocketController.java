package com.dev.realtimechat.chat.presentation;


import com.dev.realtimechat.chat.application.ChatService;
import com.dev.realtimechat.chat.domain.Chat;
import com.dev.realtimechat.shared.global.dto.ChatMessageDto;
import com.dev.realtimechat.shared.jwt.JwtProvider;
import com.dev.realtimechat.shared.jwt.TokenClaims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtProvider jwtProvider;

    @MessageMapping("/chat")
    public void createChat(
            ChatMessageDto.ChatMessageRequest request,
            @Header("token") String token
    ) {
        TokenClaims tokenClaims = jwtProvider.getTokenClaims(token);

        log.info(tokenClaims.getBojName());

        Chat chat = chatService.createChat(request, tokenClaims.getBojName(), tokenClaims.getNameTag(), tokenClaims.getIpAddress());

        messagingTemplate.convertAndSend(
                "/sub/channel/" + chat.getProblemId(),
                ChatMessageDto.ChatMessageResponse.builder()
                        .message(chat.getMessage())
                        .userName(chat.getUserName())
                        .userTier(chat.getUserTier())
                        .nameTag(chat.getNameTag())
                        .createdAt(String.valueOf(chat.getCreatedAt()))
                        .ipAddress(tokenClaims.getIpAddress())
                        .build()
        );
    }
}
