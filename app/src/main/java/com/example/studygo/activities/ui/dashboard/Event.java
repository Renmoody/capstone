package com.example.studygo.activities.ui.dashboard;

public class Event {
    private final Long eventDate;
    private String eventTime;
    private String eventName;

    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    private String eventDetails;

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
