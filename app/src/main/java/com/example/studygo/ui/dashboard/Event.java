package com.example.studygo.ui.dashboard;

import java.sql.Date;
import java.sql.Time;
import java.util.GregorianCalendar;

public class Event {
    private final Long eventDate;
    private final String eventTime;
    private final String eventName;
    private final String eventDetails;

    public Event(String eventName, String eventDetails, Long eventDate, String  eventTime) {
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

    public Long getEventDate() {
        return eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }
}
