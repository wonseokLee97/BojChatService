//package com.prod.realtimechat.chat.infrastructure.mongo;
//
//import com.prod.realtimechat.chat.domain.Chat;
//import com.prod.realtimechat.chat.infrastructure.ChatRepository;
//import com.prod.realtimechat.chat.infrastructure.springdata.springdatamongo.SpringDataMongoChatRepository;
//import lombok.RequiredArgsConstructor;
//import java.util.List;
//
//@RequiredArgsConstructor
//public class SpringDataMongoChatRepositoryImpl implements ChatRepository {
//    private final SpringDataMongoChatRepository springDataMongoChatRepository;
//
//    @Override
//    public void save(Chat chat) {
//        springDataMongoChatRepository.save(chat);
//    }
//
//    @Override
//    public List<Chat> findChatsWithPaginationByLimit(String problemId, int limit) {
//        return springDataMongoChatRepository.findChatsWithPaginationByLimit(problemId, limit);
//    }
//
//    @Override
//    public List<Chat> findChatsWithPaginationByLastMessageId(String problemId, int lastMessageId, int offset) {
//        return springDataMongoChatRepository.findChatsWithPaginationByLastMessageId(problemId, lastMessageId, offset);
//    }
//}
