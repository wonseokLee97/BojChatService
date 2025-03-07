package com.dev.realtimechat.chat.application;

import com.dev.realtimechat.chat.domain.Chat;
import com.dev.realtimechat.chat.infrastructure.ChatRepository;
import com.dev.realtimechat.shared.global.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    @Override
    public Chat createChat(ChatMessageDto.ChatMessageRequest request, String userName, String nameTag, String ipAddress) {
        String message = request.message();
        String problemId = request.problemId();
        String userTier = request.userTier();


        Chat chat = Chat.create(problemId, userName, userTier, nameTag, message, ipAddress);
        chatRepository.save(chat);

        return chat;
    }

    @Override
    public List<ChatMessageDto.ChatMessageResponse> getChatListByLimit(String problemId, int limit) {
        return chatRepository.findChatsWithPaginationByLimit(problemId, limit).stream()
                .map(ChatMessageDto.ChatMessageResponse::create) // 각 Chat 객체를 ChatMessageResponse 로 변환
                .collect(Collectors.toList()); // 변환된 결과를 List 로 수집
    }

    @Override
    public List<ChatMessageDto.ChatMessageResponse> getChatListByLastMessageId(String problemId, int limit, int lastMessageId) {
        return chatRepository.findChatsWithPaginationByLastMessageId(problemId, limit, lastMessageId).stream()
                .map(ChatMessageDto.ChatMessageResponse::create) // 각 Chat 객체를 ChatMessageResponse 로 변환
                .collect(Collectors.toList()); // 변환된 결과를 List 로 수집
    }
}
