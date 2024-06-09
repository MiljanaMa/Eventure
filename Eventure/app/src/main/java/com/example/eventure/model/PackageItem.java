package com.example.eventure.model;

public class PackageItem {
    private String serviceId;
    private Service service;
    private String employeeId;
    private User employee;
    private java.util.Date from;
    private java.util.Date to;

    public PackageItem() {
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public java.util.Date getFrom() {
        return from;
    }

    public void setFrom(java.util.Date from) {
        this.from = from;
    }

    public java.util.Date getTo() {
        return to;
    }

    public void setTo(java.util.Date to) {
        this.to = to;
    }
}
