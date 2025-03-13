package com.prod.realtimechat.chat.handler;

import com.prod.realtimechat.chat.application.SequenceGeneratorService;
import com.prod.realtimechat.chat.domain.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatListener extends AbstractMongoEventListener<Chat> {

    private final SequenceGeneratorService generatorService;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Chat> event) {
        event.getSource().setId(generatorService.generateSequence(Chat.SEQUENCE_NAME));
    }
}
