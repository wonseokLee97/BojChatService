package com.prod.realtimechat.chat.presentation;

import com.prod.realtimechat.chat.application.ChatService;
import com.prod.realtimechat.chat.domain.Chat;
import com.prod.realtimechat.shared.global.api.ApiResponse;
import com.prod.realtimechat.shared.global.dto.ChatMessageDto;
import com.prod.realtimechat.shared.global.type.http.HttpSuccessType;
import com.prod.realtimechat.shared.jwt.JwtProvider;
import com.prod.realtimechat.shared.jwt.TokenClaims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final JwtProvider jwtProvider;

    // 최초 로딩 (Limit 기반 페이징)
    @GetMapping
    public ApiResponse<?> getChatListByLimit(
            @RequestParam("problemId") String problemId,
            @RequestParam("limit") int limit
    ) {
        log.info("Get chat list By Offset");
        return ApiResponse.success(
                chatService.getChatListByLimit(problemId, limit),
                HttpSuccessType.SUCCESS_GET_CHAT_LIST);
    }

    // 추가 로딩 (lastMessageId 기반 페이징)
    @GetMapping("/lastMessageId")
    public ApiResponse<?> getChatListByLastMessageId(
            @RequestParam("problemId") String problemId,
            @RequestParam("limit") int limit,
            @RequestParam("lastMessageId") int lastMessageId
    ) {
        log.info("Get chat list By LastMessageId");
        return ApiResponse.success(
                chatService.getChatListByLastMessageId(problemId, limit, lastMessageId),
                HttpSuccessType.SUCCESS_GET_CHAT_LIST);
    }


    @PutMapping("/modify")
    public ApiResponse<?> modifyChat(
            @RequestBody ChatMessageDto.ChatMessageModifyRequest request,
            @RequestHeader("token") String token
    ) {
        TokenClaims tokenClaims = jwtProvider.getTokenClaims(token);

        Chat chat = chatService.modifyChat(request, tokenClaims.bojName());

        messagingTemplate.convertAndSend(
                "/sub/channel/modify" + chat.getProblemId(),
                ChatMessageDto.ChatMessageResponse.builder()
                        .id(chat.getId())
                        .message(chat.getMessage())
                        .userName(chat.getUserName())
                        .userTier(chat.getUserTier())
                        .nameTag(chat.getNameTag())
                        .createdAt(String.valueOf(chat.getCreatedAt()))
                        .ipAddress(tokenClaims.getIpAddress())
                        .del(chat.getDel())
                        .build()
        );

        return ApiResponse.success(
                null,
                HttpSuccessType.SUCCESS_MODIFY_CHAT);
    }

    @PutMapping("/delete")
    public ApiResponse<?> deleteChat(
            @RequestBody ChatMessageDto.ChatMessageModifyRequest request,
            @RequestHeader("token") String token
    ) {
        TokenClaims tokenClaims = jwtProvider.getTokenClaims(token);

        Chat chat = chatService.deleteChat(request, tokenClaims.bojName());

        messagingTemplate.convertAndSend(
                "/sub/channel/modify" + chat.getProblemId(),
                ChatMessageDto.ChatMessageResponse.builder()
                        .id(chat.getId())
                        .message(chat.getMessage())
                        .userName(chat.getUserName())
                        .userTier(chat.getUserTier())
                        .nameTag(chat.getNameTag())
                        .createdAt(String.valueOf(chat.getCreatedAt()))
                        .ipAddress(tokenClaims.getIpAddress())
                        .del(chat.getDel())
                        .build()
        );

        return ApiResponse.success(
                null,
                HttpSuccessType.SUCCESS_DELETE_CHAT);
    }
}
