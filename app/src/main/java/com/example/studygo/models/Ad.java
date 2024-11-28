package com.example.studygo.models;

import java.io.Serializable;
import java.util.Date;

public class Ad implements Serializable {
    public String StarTime, EndTime, Repeat, id, authorId, name, dateStart, dateEnd, details, members;
    public String Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday;
    public Date dateObjectStart, dateObjectEnd;

    public Ad() {
        Monday = Tuesday = Wednesday = Thursday  = Friday = Saturday = Sunday = "false";
    }
}
