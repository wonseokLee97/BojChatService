package com.dev.realtimechat.shared.global.dto;

public class ChatRoomDto {
    public record CreatePrivateRoomRequest(String senderId, String receiverId) {}
    public record CreatePrivateRoomResponse(String senderId, String receiverId, String roomId) {}

//    @Getter
//    public static class CreatePrivateRoomResponse {
//        private final String senderId;
//        private final String receiverId;
//        private final String problemId;
//
//        public CreatePrivateRoomResponse(String senderId, String receiverId, String problemId) {
//            this.senderId = senderId;
//            this.receiverId = receiverId;
//            this.problemId = problemId;
//        }
//    }
}
