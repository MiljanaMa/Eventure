package com.example.eventure.model;

import com.example.eventure.model.enums.ReportStatus;
import com.example.eventure.model.enums.ReportType;

import java.util.Date;

public class Report {
    private String id;
    private String reportedId;
    private Rating rating;
    private Company company;
    private User organizer;
    private String reason;
    private String rejectionReason;
    private java.util.Date createdOn;
    private ReportStatus status;
    private String reportedBy;
    private User reportedByUser;
    private ReportType type;

    public Report(String id, String reportedId, Rating rating, String reason, String rejectionReason, java.util.Date createdOn, ReportStatus status, String reportedBy, User reportedByUser) {
        this.id = id;
        this.reportedId = reportedId;
        this.rating = rating;
        this.reason = reason;
        this.rejectionReason = rejectionReason;
        this.createdOn = createdOn;
        this.status = status;
        this.reportedBy = reportedBy;
        this.reportedByUser = reportedByUser;
    }
    public Report(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReportedId() {
        return reportedId;
    }

    public void setReportedId(String reportedId) {
        this.reportedId = reportedId;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(java.util.Date createdOn) {
        this.createdOn = createdOn;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public User getReportedByUser() {
        return reportedByUser;
    }

    public void setReportedByUser(User reportedByUser) {
        this.reportedByUser = reportedByUser;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }
}
