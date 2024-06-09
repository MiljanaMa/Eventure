package com.example.eventure.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Company implements Parcelable {
    private String id;
    private String ownerId;
    private String email;
    private String name;
    private String address;
    private String phone;
    private String description;
    private List<String> imagesIds;
    private List<String> eventTypesIds;
    private List<String> categoriesIds;
    private List<WorkingHoursRecord> workingHoursRecords;

    public Company() {
    }

    public Company(String id, String ownerId, String email, String name, String address, String phone, String description, List<String> imagesIds) {
        this.id = id;
        this.ownerId = ownerId;
        this.email = email;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.description = description;
        this.imagesIds = imagesIds;
    }

    public Company(Company company) {
        this.ownerId = company.getOwnerId();
        this.email = company.getEmail();
        this.name = company.getName();
        this.address = company.getAddress();
        this.phone = company.getPhone();
        this.description = company.getDescription();
        this.imagesIds = company.getImagesIds();
        this.categoriesIds = company.getCategoriesIds();
        this.eventTypesIds = company.getEventTypesIds();
        this.workingHoursRecords = company.getWorkingHoursRecords();
    }

    public Company(Parcel in) {
        this.id = in.readString();
        this.ownerId = in.readString();
        this.email = in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.phone = in.readString();
        this.description = in.readString();
        this.imagesIds = in.readArrayList(String.class.getClassLoader());
    }

    public List<WorkingHoursRecord> getWorkingHoursRecords() {
        return workingHoursRecords;
    }

    public void setWorkingHoursRecords(List<WorkingHoursRecord> workingHoursRecords) {
        this.workingHoursRecords = workingHoursRecords;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImagesIds() {
        return imagesIds;
    }

    public void setImagesIds(List<String> imagesIds) {
        this.imagesIds = imagesIds;
    }

    public List<String> getEventTypesIds() {
        return eventTypesIds;
    }

    public void setEventTypesIds(List<String> eventTypesIds) {
        this.eventTypesIds = eventTypesIds;
    }

    public List<String> getCategoriesIds() {
        return categoriesIds;
    }

    public void setCategoriesIds(List<String> categoriesIds) {
        this.categoriesIds = categoriesIds;
    }

    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(ownerId);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(description);
        dest.writeList(imagesIds);
        dest.writeList(eventTypesIds);
        dest.writeList(categoriesIds);
    }

    public static final Parcelable.Creator<Company> CREATOR = new Parcelable.Creator<Company>() {
        @Override
        public Company createFromParcel(Parcel in) {
            return new Company(in);
        }

        @Override
        public Company[] newArray(int size) {
            return new Company[size];
        }
    };
}
