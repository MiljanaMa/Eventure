package com.example.eventure.model;

import com.example.eventure.model.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private String id;
    private String name;
    private Date date;
    private Time from;
    private Time to;
    private AppointmentStatus status;
    private String employeeId;
    private User employee;

    public Appointment(String name) {
        this.name = name;
    }
    public Appointment() {

    }
    public Appointment(String id, String name, Date date, Time from, Time to, AppointmentStatus status, String employee) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.from = from;
        this.to = to;
        this.status = status;
        this.employeeId = employee;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Time getFrom() {
        return from;
    }

    public void setFrom(Time from) {
        this.from = from;
    }

    public Time getTo() {
        return to;
    }

    public void setTo(Time to) {
        this.to = to;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
