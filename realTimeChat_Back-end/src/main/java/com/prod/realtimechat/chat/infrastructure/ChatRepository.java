package com.prod.realtimechat.chat.infrastructure;

import com.prod.realtimechat.chat.domain.Chat;

import java.util.List;

public interface ChatRepository {
    void save(Chat chat);
    Chat modify(Long id, String message);
    Chat deleteChat(Long id);
    Chat findById(Long id);
    List<Chat> findChatsWithPaginationByLimit(String problemId, int limit);
    List<Chat> findChatsWithPaginationByLastMessageId(String problemId, int lastMessageId, int offset);
}
