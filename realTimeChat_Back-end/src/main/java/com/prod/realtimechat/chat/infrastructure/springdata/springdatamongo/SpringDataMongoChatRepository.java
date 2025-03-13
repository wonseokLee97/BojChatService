package com.prod.realtimechat.chat.infrastructure.springdata.springdatamongo;

import com.prod.realtimechat.chat.domain.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface SpringDataMongoChatRepository extends MongoRepository<Chat, String> {

    // Offset 기반 페이징 (List로 반환)
    @Query("{ 'chatroomId': ?0 }") // chatroomId 기준으로 필터링
    List<Chat> findChatsWithPaginationByLimit(String problemId, int limit);

    // lastMessageId 기반 페이징 (List로 반환)
    @Query("{ 'chatroomId': ?0, 'id': { $lt: ?1 } }") // chatroomId 기준 필터링, lastMessageId보다 작은 id
    List<Chat> findChatsWithPaginationByLastMessageId(String problemId, int lastMessageId, int limit);
}