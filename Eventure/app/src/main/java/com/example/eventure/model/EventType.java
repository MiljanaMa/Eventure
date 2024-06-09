package com.example.eventure.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class EventType implements Parcelable {

    private String id;
    private String name;
    private String description;
    private List<String> suggestedSubcategoriesIds;
    private Boolean deactivated;

    public EventType() {
    }

    public EventType(String id, String name, String description, List<String> suggestedSubcategoriesIds, Boolean deactivated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.suggestedSubcategoriesIds = suggestedSubcategoriesIds;
        this.deactivated = deactivated;
    }

    public EventType(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.suggestedSubcategoriesIds = in.readArrayList(String.class.getClassLoader());
        this.deactivated = in.readBoolean();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSuggestedSubcategoriesIds() {
        return suggestedSubcategoriesIds;
    }

    public void setSuggestedSubcategoriesIds(List<String> suggestedSubcategoriesIds) {
        this.suggestedSubcategoriesIds = suggestedSubcategoriesIds;
    }

    @Override
    public String toString() {
        return name;
    }

    public Boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(Boolean deactivated) {
        this.deactivated = deactivated;
    }

    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeList(suggestedSubcategoriesIds);
        dest.writeBoolean(deactivated);
    }

    public static final Parcelable.Creator<EventType> CREATOR = new Parcelable.Creator<EventType>() {
        @Override
        public EventType createFromParcel(Parcel in) {
            return new EventType(in);
        }

        @Override
        public EventType[] newArray(int size) {
            return new EventType[size];
        }
    };
}