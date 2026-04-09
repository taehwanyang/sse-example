package com.ythwork.sseexample.sse;

public class BroadcastMessage {

    private String eventName;
    private String message;
    private Long timestamp;
    private String senderInstanceId;

    public BroadcastMessage() {
    }

    public BroadcastMessage(String eventName, String message, Long timestamp, String senderInstanceId) {
        this.eventName = eventName;
        this.message = message;
        this.timestamp = timestamp;
        this.senderInstanceId = senderInstanceId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderInstanceId() {
        return senderInstanceId;
    }

    public void setSenderInstanceId(String senderInstanceId) {
        this.senderInstanceId = senderInstanceId;
    }
}
