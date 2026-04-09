package com.ythwork.sseexample.sse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SseService {

    private final SseEmitterRepository repository;

    @Value("${app.sse.timeout}")
    private long timeout;

    public SseService(SseEmitterRepository repository) {
        this.repository = repository;
    }

    public SseEmitter subscribe() {
        String clientId = UUID.randomUUID().toString();
        SseEmitter emitter = new SseEmitter(timeout);

        repository.save(clientId, emitter);

        emitter.onCompletion(() -> repository.remove(clientId));

        emitter.onTimeout(() -> {
            repository.remove(clientId);
            emitter.complete();
        });

        emitter.onError(ex -> repository.remove(clientId));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected: " + clientId));
        } catch (IOException | IllegalStateException ex) {
            repository.remove(clientId);
            emitter.completeWithError(ex);
        }

        return emitter;
    }

    public void broadcastLocal(String eventName, Object data) {
        List<String> deadClients = new ArrayList<>();

        repository.findAll().forEach((clientId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (IOException | IllegalStateException ex) {
                deadClients.add(clientId);
            }
        });

        deadClients.forEach(clientId -> {
            SseEmitter emitter = repository.findAll().remove(clientId);
            if (emitter != null) {
                emitter.complete();
            }
        });
    }

    public int count() {
        return repository.count();
    }
}
