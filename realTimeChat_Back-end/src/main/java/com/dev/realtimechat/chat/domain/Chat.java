package com.dev.realtimechat.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Document(collection = "chat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Chat {

    @Transient
    public static final String SEQUENCE_NAME = "chat_sequence";

    @Setter
    @Id
    private Long id;

    @Field("problem_id")
    private String problemId;

    @Field("user_name")
    private String userName;

    @Field("user_tier")
    private String userTier;

    @Field("name_tag")
    private String nameTag;

    @Field("ip_address")
    private String ipAddress;

    @Field("message")
    private String message;

    @Field("created_at")
    private LocalDateTime createdAt;

    // 정적 팩터리 메서드, 도메인 패턴
    @Builder
    private Chat(String problemId, String userName, String userTier, String nameTag, String message, String ipAddress, LocalDateTime createdAt) {
        this.problemId = problemId;
        this.userName = userName;
        this.userTier = userTier;
        this.nameTag = nameTag;
        this.message = message;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
    }

    public static Chat create(String problemId, String userName, String userTier, String nameTag, String message, String ipAddress) {
        return Chat.builder()
                .problemId(problemId)
                .userName(userName)
                .userTier(userTier)
                .nameTag(nameTag)
                .message(message)
                .ipAddress(ipAddress)
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();
    }

}
