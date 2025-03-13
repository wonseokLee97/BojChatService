package com.prod.realtimechat.chat.application;

import com.prod.realtimechat.chat.domain.Chat;
import com.prod.realtimechat.chat.exception.ChatException;
import com.prod.realtimechat.chat.infrastructure.ChatRepository;
import com.prod.realtimechat.shared.global.dto.ChatMessageDto;
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
    private final XssSanitizer sanitizer;

    @Override
    public Chat createChat(ChatMessageDto.ChatMessageRequest request, String bojName, String nameTag, String ipAddress) {
        String message = sanitizer.sanitize(request.message());
        String problemId = request.problemId();
        String userTier = request.userTier();

        Chat chat = Chat.create(problemId, bojName, userTier, nameTag, message, ipAddress);
        chatRepository.save(chat);

        return chat;
    }

    @Override
    public Chat modifyChat(ChatMessageDto.ChatMessageModifyRequest request, String bojName) {
        Long id = request.id();
        Chat chat = chatRepository.findById(id);

        if (!chat.getUserName().equals(bojName)) {
            throw new ChatException.UnauthorizedActionException();
        }

        String message = sanitizer.sanitize(request.message());

        return chatRepository.modify(id, message);
    }

    @Override
    public Chat deleteChat(ChatMessageDto.ChatMessageModifyRequest request, String bojName) {
        Long id = request.id();
        Chat chat = chatRepository.findById(id);

        if (!chat.getUserName().equals(bojName)) {
            throw new ChatException.UnauthorizedActionException();
        }

        return chatRepository.deleteChat(id);
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
