package com.example.studygo.models;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
    public String access, date, time, name, details, id, groupId, authorId;
    public int members = 1;
    public Date dateObject;
    public Event() {}

    public void setAccess(String access) {
        this.access = access;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetails(String details) {
        this.details = details;
    }



    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
    }

}
