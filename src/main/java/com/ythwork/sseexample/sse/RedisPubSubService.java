package com.ythwork.sseexample.sse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisPubSubService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.redis.channel}")
    private String channel;

    public RedisPubSubService(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(BroadcastMessage message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            stringRedisTemplate.convertAndSend(channel, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize broadcast message", e);
        }
    }
}
