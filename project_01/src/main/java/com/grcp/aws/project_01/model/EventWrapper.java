package com.grcp.aws.project_01.model;

import com.grcp.aws.project_01.enums.EventType;

public class EventWrapper {

    private EventType eventType;
    private String data;

    public EventWrapper(EventType eventType, String data) {
        this.eventType = eventType;
        this.data = data;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getData() {
        return data;
    }
}
