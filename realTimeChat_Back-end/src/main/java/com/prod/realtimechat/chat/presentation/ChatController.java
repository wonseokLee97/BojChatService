package com.dev.realtimechat.chat.presentation;

import com.dev.realtimechat.chat.application.ChatService;
import com.dev.realtimechat.shared.global.api.ApiResponse;
import com.dev.realtimechat.shared.global.type.http.HttpSuccessType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

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
}
