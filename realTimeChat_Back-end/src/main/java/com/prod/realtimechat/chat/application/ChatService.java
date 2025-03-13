package com.prod.realtimechat.chat.application;

import com.prod.realtimechat.chat.domain.Chat;
import com.prod.realtimechat.shared.global.dto.ChatMessageDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ChatService {
    Chat createChat(ChatMessageDto.ChatMessageRequest request, String bojName, String nameTag, String ipAddress);
    Chat modifyChat(ChatMessageDto.ChatMessageModifyRequest request, String bojName);
    Chat deleteChat(ChatMessageDto.ChatMessageModifyRequest request, String bojName);
    List<ChatMessageDto.ChatMessageResponse> getChatListByLimit(String problemId, int limit);
    List<ChatMessageDto.ChatMessageResponse> getChatListByLastMessageId(String problemId, int limit, int lastMessageId);
}
