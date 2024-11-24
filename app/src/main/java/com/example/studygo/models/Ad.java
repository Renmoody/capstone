package com.example.studygo.models;

import java.io.Serializable;
import java.util.Date;

public class Ad implements Serializable {
    public String StarTime, EndTime, Repeat, Monday,
            Tuesday, Wednesday, Thursday, Friday,
            Saturday, Sunday, id, authorId, name, dateStart, dateEnd, details;
    public Date dateObjectStart, dateObjectEnd;
    public Ad() {}
}
