package com.example.eventure.model;

import java.util.List;

public class EmployeeWorkingHours {
    private Date fromDate;
    private Date toDate;
    private List<WorkingHoursRecord> weeklySchedule;

    public EmployeeWorkingHours() {
    }

    public EmployeeWorkingHours(Date fromDate, Date toDate, List<WorkingHoursRecord> weeklySchedule) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.weeklySchedule = weeklySchedule;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public List<WorkingHoursRecord> getWeeklySchedule() {
        return weeklySchedule;
    }

    public void setWeeklySchedule(List<WorkingHoursRecord> weeklySchedule) {
        this.weeklySchedule = weeklySchedule;
    }
    public boolean isOverlapping(EmployeeWorkingHours other) {
        if(this.fromDate.compareTo(other.fromDate) >= 0 && this.fromDate.compareTo(other.toDate) <= 0)
            return true;
        if(this.toDate.compareTo(other.fromDate) >= 0 && this.toDate.compareTo(other.toDate) <= 0)
            return true;
        return false;
    }
}
