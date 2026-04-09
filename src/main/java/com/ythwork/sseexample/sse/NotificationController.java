package com.ythwork.sseexample.sse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final RedisPubSubService redisPubSubService;
    private final SseService sseService;

    @Value("${server.port}")
    private String port;

    public NotificationController(RedisPubSubService redisPubSubService, SseService sseService) {
        this.redisPubSubService = redisPubSubService;
        this.sseService = sseService;
    }

    @PostMapping("/broadcast")
    public String broadcast(@RequestBody NotificationRequest request) {
        BroadcastMessage message = new BroadcastMessage(
                "notification",
                request.getMessage(),
                System.currentTimeMillis(),
                "server-" + port
        );

        redisPubSubService.publish(message);

        return "published. local connected clients = " + sseService.count();
    }

    @GetMapping("/count")
    public String count() {
        return "connected clients: " + sseService.count();
    }

    public static class NotificationRequest {
        private String message;

        public NotificationRequest() {
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
