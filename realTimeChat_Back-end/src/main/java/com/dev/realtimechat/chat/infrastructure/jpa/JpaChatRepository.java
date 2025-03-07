package com.dev.realtimechat.chat.infrastructure.jpa;

//import com.dev.realtimechat.chat.domain.ChatJpa;
//import com.dev.realtimechat.chat.infrastructure.ChatRepository;
//import com.dev.realtimechat.chat.infrastructure.springdata.springdatajpa.SpringDataJpaChatRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//import java.util.List;

//@Repository
//@RequiredArgsConstructor
//public class JpaChatRepository implements ChatRepository {
//    private final SpringDataJpaChatRepository springDataJpaChatRepository;
//
//    @Override
//    public void save(ChatJpa chat) {
//        springDataJpaChatRepository.save(chat);
//    }
//
//    @Override
//    public List<ChatJpa> findChatsWithPaginationByOffset(String problemId, int limit, int offset) {
//        return springDataJpaChatRepository.findChatsWithPaginationByOffset(problemId, limit, offset);
//    }
//
//    @Override
//    public List<ChatJpa> findChatsWithPaginationByLastMessageId(String problemId, int limit, int lastMessageId) {
//        return springDataJpaChatRepository.findChatsWithPaginationByLastMessageId(problemId, limit, lastMessageId);
//    }
//}
