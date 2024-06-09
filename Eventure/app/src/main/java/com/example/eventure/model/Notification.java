package com.example.eventure.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventure.model.enums.NotificationStatus;

public class Notification implements Parcelable {
    private String id;
    private String title;
    private String message;
    private String receiverId;
    private String senderId;
    private NotificationStatus status;

    public Notification() {
    }

    public Notification(String id, String title, String message, String receiverId, String senderId, NotificationStatus status) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.status = status;
    }

    public Notification(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.message = in.readString();
        this.receiverId = in.readString();
        this.senderId = in.readString();
        this.status = NotificationStatus.valueOf(in.readString());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(message);
        dest.writeString(receiverId);
        dest.writeString(senderId);
        if (status != null) {
            dest.writeString(status.name());
        } else {
            dest.writeString(null);
        }
    }

    public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };
}
