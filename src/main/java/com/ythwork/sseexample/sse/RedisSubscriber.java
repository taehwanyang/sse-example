package com.ythwork.sseexample.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisSubscriber implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(RedisSubscriber.class);

    private final ObjectMapper objectMapper;
    private final SseService sseService;

    public RedisSubscriber(ObjectMapper objectMapper, SseService sseService) {
        this.objectMapper = objectMapper;
        this.sseService = sseService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body = new String(message.getBody());
            BroadcastMessage broadcastMessage =
                    objectMapper.readValue(body, BroadcastMessage.class);

            log.info("Received redis message eventName={}, senderInstanceId={}, message={}",
                    broadcastMessage.getEventName(),
                    broadcastMessage.getSenderInstanceId(),
                    broadcastMessage.getMessage());

            sseService.broadcastLocal(
                    broadcastMessage.getEventName(),
                    broadcastMessage
            );
        } catch (Exception e) {
            log.error("Failed to process redis message", e);
        }
    }
}
