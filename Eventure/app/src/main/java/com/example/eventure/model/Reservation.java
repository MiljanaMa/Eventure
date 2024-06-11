package com.example.eventure.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventure.model.enums.ReservationStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Reservation implements Parcelable {
    private String id;
    private String organizerId;
    private User organizer;
    private String employeeId;
    private User employee;
    private String serviceId;
    private Service service;
    private java.util.Date from;
    private java.util.Date to;
    private ReservationStatus status;
    private String packageId;
    private Package eventPackage;
    private java.util.Date cancellationDeadline;
    private Boolean hasRating;
    private String companyId;
    private String eventId;
    private boolean isNotified;
    private Date cancellationDate;
    private List<String> appointmentIds;
    private List<String> acceptedEmployeeIds;
    public Reservation(){};

    public Reservation(String id, String organizerId, User organizer, String employeeId, User employee, String serviceId,
                       Service service, Date from, Date to, ReservationStatus status, String packageId, Date cancellationDeadline,
                       Boolean hasRating, String companyId, String eventId) {
        this.id = id;
        this.organizerId = organizerId;
        this.organizer = organizer;
        this.employeeId = employeeId;
        this.employee = employee;
        this.serviceId = serviceId;
        this.service = service;
        this.from = from;
        this.to = to;
        this.status = status;
        this.packageId = packageId;
        this.cancellationDeadline = cancellationDeadline;
        this.hasRating = hasRating;
        this.companyId = companyId;
        this.eventId = eventId;
        this.isNotified = false;
        this.cancellationDate = from;
        appointmentIds = new ArrayList<>();
        acceptedEmployeeIds = new ArrayList<>();
    }

    public Reservation(Reservation reservation) {
        this.id = reservation.id;
        this.organizerId = reservation.organizerId;
        this.organizer = reservation.organizer;
        this.employeeId = reservation.employeeId;
        this.employee = reservation.employee;
        this.serviceId = reservation.serviceId;
        this.service = reservation.service;
        this.from = (reservation.from != null) ? new java.util.Date(reservation.from.getTime()) : null;
        this.to = (reservation.to != null) ? new java.util.Date(reservation.to.getTime()) : null;
        this.status = reservation.status;
        this.packageId = reservation.packageId;
        this.cancellationDeadline = (reservation.cancellationDeadline != null) ? new java.util.Date(reservation.cancellationDeadline.getTime()) : null;
        this.hasRating = reservation.hasRating;
        this.companyId = reservation.companyId;
        this.isNotified = reservation.isNotified;
        this.cancellationDate = reservation.cancellationDate;
        this.appointmentIds = reservation.appointmentIds;
        this.eventPackage = reservation.eventPackage;
        this.acceptedEmployeeIds = reservation.acceptedEmployeeIds;
        this.eventId = reservation.eventId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public List<String> getAcceptedEmployeeIds() {
        return acceptedEmployeeIds;
    }

    public void setAcceptedEmployeeIds(List<String> acceptedEmployeeIds) {
        this.acceptedEmployeeIds = acceptedEmployeeIds;
    }

    public Package getEventPackage() {
        return eventPackage;
    }

    public void setEventPackage(Package eventPackage) {
        this.eventPackage = eventPackage;
    }

    public List<String> getAppointmentIds() {
        return appointmentIds;
    }

    public void setAppointmentIds(List<String> appointmentIds) {
        this.appointmentIds = appointmentIds;
    }

    public Date getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(Date cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public Boolean getHasRating() {
        return hasRating;
    }

    public void setHasRating(Boolean hasRating) {
        this.hasRating = hasRating;
    }

    public boolean isNotified() {
        return isNotified;
    }

    public void setNotified(boolean notified) {
        isNotified = notified;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CharSequence getStatus() {
        return status.toString();
    }
    public ReservationStatus getEnumStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
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

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Date getCancellationDeadline() {
        return cancellationDeadline;
    }

    public void setCancellationDeadline(Date cancellationDeadline) {
        this.cancellationDeadline = cancellationDeadline;
    }

    public Boolean getGraded() {
        return hasRating;
    }

    public void setGraded(Boolean graded) {
        hasRating = graded;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(companyId);
        dest.writeString(String.valueOf(status));
        dest.writeString(packageId);
        dest.writeString(serviceId);
        //fill more fields
    }
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

}
