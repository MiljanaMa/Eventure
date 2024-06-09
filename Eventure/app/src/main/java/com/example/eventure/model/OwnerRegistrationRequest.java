package com.example.eventure.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.acl.Owner;
import java.util.Date;

import androidx.annotation.NonNull;

import com.example.eventure.model.enums.ApprovalStatus;

public class OwnerRegistrationRequest implements Parcelable{
    private String id;
    private User owner;
    private Company company;
    private String rejectionReason;
    private ApprovalStatus status;
    private Date submissionDate;

    public OwnerRegistrationRequest(){}

    public OwnerRegistrationRequest(String email, User owner) {
        this.owner = owner;
        this.submissionDate = new Date();
        this.status = ApprovalStatus.PENDING;
    }

    protected OwnerRegistrationRequest(Parcel in) {
        this.id = in.readString();
        this.owner = in.readParcelable(User.class.getClassLoader());
        this.company = in.readParcelable(Company.class.getClassLoader());
        this.rejectionReason = in.readString();
        this.status = ApprovalStatus.valueOf(in.readString());
        long dateLong = in.readLong();
        this.submissionDate = (dateLong != -1) ? new Date(dateLong) : null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(owner, 0);
        dest.writeParcelable(company, 0);
        dest.writeString(rejectionReason);
        if (status != null) {
            dest.writeString(status.name());
        } else {
            dest.writeString(null);
        }
        dest.writeLong(submissionDate != null ? submissionDate.getTime() : -1);
    }

    public static final Creator<OwnerRegistrationRequest> CREATOR = new Creator<OwnerRegistrationRequest>() {
        @Override
        public OwnerRegistrationRequest createFromParcel(Parcel in) {
            return new OwnerRegistrationRequest(in);
        }

        @Override
        public OwnerRegistrationRequest[] newArray(int size) {
            return new OwnerRegistrationRequest[size];
        }
    };

}
