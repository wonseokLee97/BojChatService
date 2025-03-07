//package com.dev.realtimechat.chat.infrastructure.springdata.springdatajpa;
//
//import com.dev.realtimechat.chat.domain.ChatJpa;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import java.util.List;
//
//public interface SpringDataJpaChatRepository extends JpaRepository<ChatJpa, Long> {
//
//    @Query(value = "SELECT * " +
//            "FROM chat " +
//            "WHERE chatroom_id = :problemId " +
//            "ORDER BY created_at DESC " +
//            "LIMIT :limit OFFSET :offset", nativeQuery = true)
//    List<ChatJpa> findChatsWithPaginationByOffset(
//            @Param("problemId") String problemId,
//            @Param("limit") int limit,
//            @Param("offset") int offset
//    );
//    // offset: 어디서 부터
//    // limit: 몇 개 까지
//
//
//    @Query(value = "SELECT * " +
//            "FROM chat " +
//            "WHERE chatroom_id = :problemId " +
//            "AND id < :lastMessageId " +
//            "ORDER BY created_at DESC " +
//            "LIMIT :limit", nativeQuery = true)
//    List<ChatJpa> findChatsWithPaginationByLastMessageId(
//            @Param("problemId") String problemId,
//            @Param("limit") int limit,
//            @Param("lastMessageId") int lastMessageId
//    );
//}


