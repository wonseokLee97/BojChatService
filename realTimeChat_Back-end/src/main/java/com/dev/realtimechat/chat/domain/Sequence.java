package com.dev.realtimechat.chat.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "counter")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Sequence {
    @Id
    private String id;
    private int seq;
}
