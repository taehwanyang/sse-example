package com.ythwork.sseexample.sse;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseEmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void save(String clientId, SseEmitter emitter) {
        emitters.put(clientId, emitter);
    }

    public void remove(String clientId) {
        emitters.remove(clientId);
    }

    public Map<String, SseEmitter> findAll() {
        return emitters;
    }

    public int count() {
        return emitters.size();
    }
}
