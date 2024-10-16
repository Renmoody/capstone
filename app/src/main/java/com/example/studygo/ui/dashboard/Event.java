package com.example.studygo.ui.dashboard;

import java.sql.Time;

public class Event {
    private final long eventDate;
    private final Time eventTime;
    private final String eventName;
    private final String eventDetails;

    public Event(String eventName, String eventDetails, long eventDate, Time  eventTime) {
        this.eventName = eventName;
        this.eventDetails = eventDetails;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDetails() {
        return eventDetails;
    }

    public long getEventDate() {
        return eventDate;
    }

    public Time getEventTime() {
        return eventTime;
    }
}
