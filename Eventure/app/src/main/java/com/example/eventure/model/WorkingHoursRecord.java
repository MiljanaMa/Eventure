package com.example.eventure.model;

import com.example.eventure.model.enums.DayOfWeek;

public class WorkingHoursRecord {
    private DayOfWeek dayOfWeek;
    private Time fromTime;
    private Time toTime;

    public WorkingHoursRecord() {
    }

    public WorkingHoursRecord(DayOfWeek dayOfWeek, Time fromTime, Time toTime) {
        this.dayOfWeek = dayOfWeek;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Time getFromTime() {
        return fromTime;
    }

    public void setFromTime(Time fromTime) {
        this.fromTime = fromTime;
    }

    public Time getToTime() {
        return toTime;
    }

    public void setToTime(Time toTime) {
        this.toTime = toTime;
    }
}
