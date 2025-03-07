package com.dev.realtimechat.chat.domain;

//import com.dev.realtimechat.chatroom.domain.ChatRoom;
//import jakarta.persistence.*;
//import lombok.*;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//import java.time.LocalDateTime;
//
//@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@EntityListeners(AuditingEntityListener.class)
//@Getter
//@Table(name = "chat")
//public class ChatJpa extends ChatDomain {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
//
//    @ManyToOne
//    @JoinColumn(name = "chatroom_id")
//    private ChatRoom chatRoom;
//
//    @Column(columnDefinition = "TEXT")
//    private String message;
//
//    @CreatedDate
//    private LocalDateTime createdAt;
//
//    // 정적 팩터리 메서드, 도메인 패턴, 어뎁터 패턴
//    @Builder
//    private ChatJpa(ChatRoom chatRoom, String randId, String nameTag, String message, String ipAddress) {
//        this.chatRoom = chatRoom;
//        this.randId = randId;
//        this.nameTag = nameTag;
//        this.message = message;
//        this.ipAddress = ipAddress;
//    }
//
//    // Factory Method Pattern
//    public static ChatJpa create(ChatRoom chatRoom, String randId, String nameTag, String message, String ipAddress) {
//        return ChatJpa.builder()
//                .chatRoom(chatRoom)
//                .randId(randId)
//                .nameTag(nameTag)
//                .message(message)
//                .ipAddress(ipAddress)
//                .build();
//    }
//
//    // Adapter Pattern
//    public static ChatJpa from(ChatDomain chatDomain) {
//        return ChatJpa.builder()
//                .chatRoom(chatDomain.getChatRoom())
//                .randId(chatDomain.getRandId())
//                .nameTag(chatDomain.getNameTag())
//                .message(chatDomain.getMessage())
//                .ipAddress(chatDomain.getIpAddress())
//                .build();
//    }
//
//    // Adapter Pattern
//    public static ChatDomain to(ChatJpa chatJpa) {
//        return ChatJpa.builder()
//                .chatRoom(chatJpa.getChatRoom())
//                .randId(chatJpa.getRandId())
//                .nameTag(chatJpa.getNameTag())
//                .message(chatJpa.getMessage())
//                .ipAddress(chatJpa.getIpAddress())
//                .build();
//    }
//}
