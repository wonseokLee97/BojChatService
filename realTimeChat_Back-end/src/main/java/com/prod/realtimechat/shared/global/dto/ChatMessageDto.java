package com.prod.realtimechat.shared.global.dto;

import com.prod.realtimechat.chat.domain.Chat;
import lombok.*;

public class ChatMessageDto {
    public record ChatMessageRequest(String problemId, String message, String userName, String userTier, String nameTag, String ipAddress) {}
    public record ChatMessageModifyRequest(long id, String message) {}

    @Data
    public static class ChatMessageResponse {
        private final long id;
        private final String message;
        private final String userName;
        private final String userTier;
        private final String nameTag;
        private final String createdAt;
        private final String ipAddress;
        private final String problemId;
        private final int del;

        // 정적 팩터리 메서드, 도메인 패턴
        @Builder
        private ChatMessageResponse(long id, String problemId, String message, String userName, String userTier, String nameTag, String createdAt, String ipAddress, int del) {
            this.id = id;
            this.message = message;
            this.userName = userName;
            this.userTier = userTier;
            this.nameTag = nameTag;
            this.createdAt = createdAt;
            this.ipAddress = ipAddress;
            this.problemId = problemId;
            this.del = del;
        }

        public static ChatMessageResponse create(Chat chat) {
            return ChatMessageResponse.builder()
                    .id(chat.getId())
                    .message(chat.getMessage())
                    .userName(chat.getUserName())
                    .userTier(chat.getUserTier())
                    .nameTag(chat.getNameTag())
                    .createdAt(String.valueOf(chat.getCreatedAt()))
                    .ipAddress(chat.getIpAddress())
                    .problemId(chat.getProblemId())
                    .del(chat.getDel())
                    .build();
        }
    }
}
