package com.dev.realtimechat.chat.application;

import com.dev.realtimechat.chat.domain.Chat;
import com.dev.realtimechat.shared.global.dto.ChatMessageDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ChatService {
    Chat createChat(ChatMessageDto.ChatMessageRequest request, String userName, String nameTag, String ipAddress);
    List<ChatMessageDto.ChatMessageResponse> getChatListByLimit(String problemId, int limit);
    List<ChatMessageDto.ChatMessageResponse> getChatListByLastMessageId(String problemId, int limit, int lastMessageId);
}
