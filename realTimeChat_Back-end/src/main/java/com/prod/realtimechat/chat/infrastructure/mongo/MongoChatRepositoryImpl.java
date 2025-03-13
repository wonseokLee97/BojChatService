package com.prod.realtimechat.chat.infrastructure.mongo;

import com.prod.realtimechat.chat.domain.Chat;
import com.prod.realtimechat.chat.infrastructure.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
@Slf4j
public class MongoChatRepositoryImpl implements ChatRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public void save(Chat chat) {
        mongoTemplate.save(chat);
    }

    @Override
    public Chat modify(Long id, String message) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("message", message);

        return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), Chat.class);
    }

    @Override
    public Chat deleteChat(Long id) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("del", 1);

        return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), Chat.class);
    }

    @Override
    public Chat findById(Long id) {
        Query query = new Query(Criteria.where("_id").is(id));

        return mongoTemplate.findOne(query, Chat.class);
    }

    // 최초 로딩 시점
    // SELECT * FROM chat WHERE chat_room_id = 'roomId' ORDER BY _id DESC LIMIT 10
    @Override
    public List<Chat> findChatsWithPaginationByLimit(String problemId, int limit) {
        Query query = new Query(Criteria.where("problem_id").is(problemId))
                .with(Sort.by(Sort.Direction.DESC, "_id"))  // _id 기준 내림차순 정렬 추가
                .limit(limit);
        return mongoTemplate.find(query, Chat.class);
    }

    // 이후 스크롤 시점
    // SELECT * FROM chat
    // WHERE chat_room_id = 'roomId' AND _id < lastMessageId
    // ORDER BY _id DESC LIMIT 10;
    @Override
    public List<Chat> findChatsWithPaginationByLastMessageId(String problemId, int limit, int lastMessageId) {
        Query query = new Query(Criteria.where("problem_id").is(problemId)
                .and("_id").lt(lastMessageId))
                .with(Sort.by(Sort.Direction.DESC, "_id"))  // _id 기준 내림차순 정렬 추가
                .limit(limit);
        return mongoTemplate.find(query, Chat.class);
    }
}