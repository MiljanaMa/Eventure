package com.example.eventure.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventure.model.enums.ConfirmationType;

import java.util.List;

public class Service implements Parcelable {
    private String id;
    private String categoryId;
    private String subcategoryId;
    private String name;
    private String description;
    private List<String> imagesIds;
    private String specifics;
    private double pricePerHour;
    private double fullPrice;
    private double duration;
    private String location;
    private double discount;
    private List<String> serviceProviders;
    private List<String> eventTypesIds;
    private int reservationDeadlineInDays;
    private int cancellationDeadlineInDays;
    private Boolean manualConfirmation;
    private Boolean available;
    private Boolean visible;
    private ConfirmationType confirmationType;
    private String companyId;

    public Service() {
    }

    public Service(String id, String category, String subcategory, String name, String description, List<String> imagesIds, String specifics,
                   double pricePerHour, double fullPrice, double duration, String location, double discount, List<String> serviceProviders,
                   List<String> eventTypes, int reservationDeadlineInDays, int cancellationDeadlineInDays, Boolean manualConfirmation, Boolean available,
                   Boolean visible, String companyId) {

        this.id = id;
        this.categoryId = category;
        this.subcategoryId = subcategory;
        this.name = name;
        this.description = description;
        this.imagesIds = imagesIds;
        this.specifics = specifics;
        this.pricePerHour = pricePerHour;
        this.fullPrice = fullPrice;
        this.duration = duration;
        this.location = location;
        this.discount = discount;
        this.serviceProviders = serviceProviders;
        this.eventTypesIds = eventTypes;
        this.reservationDeadlineInDays = reservationDeadlineInDays;
        this.cancellationDeadlineInDays = cancellationDeadlineInDays;
        this.manualConfirmation = manualConfirmation;
        this.available = available;
        this.visible = visible;
        this.companyId = companyId;
    }

    public Service(Parcel in) {

        this.id = in.readString();
        this.categoryId = in.readString();
        this.subcategoryId = in.readString();
        this.name = in.readString();
        this.companyId = in.readString();
        this.description = in.readString();
        this.imagesIds = in.readArrayList(String.class.getClassLoader());
        this.specifics = in.readString();;
        this.pricePerHour = in.readDouble();
        this.fullPrice = in.readDouble();
        this.duration = in.readDouble();
        this.location = in.readString();
        this.discount = in.readDouble();
        this.serviceProviders = in.readArrayList(String.class.getClassLoader());
        this.eventTypesIds = in.readArrayList(String.class.getClassLoader());
        this.reservationDeadlineInDays = in.readInt();
        this.cancellationDeadlineInDays = in.readInt();
        byte availableVal = in.readByte();
        available = availableVal == 0x02 ? null : availableVal != 0x00;
        byte visibleVal = in.readByte();
        visible = visibleVal == 0x02 ? null : visibleVal != 0x00;
        byte confirmationVal = in.readByte();
        manualConfirmation = confirmationVal == 0x02 ? null : confirmationVal != 0x00;
    }

    public ConfirmationType getConfirmationType() {
        return confirmationType;
    }

    public void setConfirmationType(ConfirmationType confirmationType) {
        this.confirmationType = confirmationType;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(String subcategoryId) {
        this.subcategoryId = subcategoryId;
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

    public List<String> getImagesIds() {
        return imagesIds;
    }

    public void setImagesIds(List<String> imagesIds) {
        this.imagesIds = imagesIds;
    }

    public String getSpecifics() {
        return specifics;
    }

    public void setSpecifics(String specifics) {
        this.specifics = specifics;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public double getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(double fullPrice) {
        this.fullPrice = fullPrice;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public List<String> getServiceProviders() {
        return serviceProviders;
    }

    public void setServiceProviders(List<String> serviceProviders) {
        this.serviceProviders = serviceProviders;
    }

    public List<String> getEventTypesIds() {
        return eventTypesIds;
    }

    public void setEventTypesIds(List<String> eventTypesIds) {
        this.eventTypesIds = eventTypesIds;
    }

    public int getReservationDeadlineInDays() {
        return reservationDeadlineInDays;
    }

    public void setReservationDeadlineInDays(int reservationDeadlineInDays) {
        this.reservationDeadlineInDays = reservationDeadlineInDays;
    }

    public int getCancellationDeadlineInDays() {
        return cancellationDeadlineInDays;
    }

    public void setCancellationDeadlineInDays(int cancellationDeadlineInDays) {
        this.cancellationDeadlineInDays = cancellationDeadlineInDays;
    }

    public Boolean getManualConfirmation() {
        return manualConfirmation;
    }

    public void setManualConfirmation(Boolean manualConfirmation) {
        this.manualConfirmation = manualConfirmation;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", category='" + categoryId + '\'' +
                ", subcategory='" + subcategoryId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", images=" + imagesIds +
                ", specifics='" + specifics + '\'' +
                ", pricePerHour=" + pricePerHour +
                ", fullPrice=" + fullPrice +
                ", duration=" + duration +
                ", location='" + location + '\'' +
                ", discount=" + discount +
                ", serviceProviders=" + serviceProviders +
                ", eventType='" + eventTypesIds + '\'' +
                ", reservationDeadlineInDays=" + reservationDeadlineInDays +
                ", cancellationDeadlineInDays=" + cancellationDeadlineInDays +
                ", manualConfirmation=" + manualConfirmation +
                ", available=" + available +
                ", visible=" + visible +
                '}';
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(categoryId);
        dest.writeString(subcategoryId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeList(imagesIds);
        dest.writeString(specifics);
        dest.writeDouble(pricePerHour);
        dest.writeDouble(fullPrice);
        dest.writeDouble(duration);
        dest.writeString(location);
        dest.writeDouble(discount);
        dest.writeList(serviceProviders);
        dest.writeList(eventTypesIds);
        dest.writeList(eventTypesIds);
        dest.writeInt(reservationDeadlineInDays);
        dest.writeInt(cancellationDeadlineInDays);
        if (manualConfirmation == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (manualConfirmation ? 0x01 : 0x00));
        }
        if (available == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (available ? 0x01 : 0x00));
        }
        if (visible == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (visible ? 0x01 : 0x00));
        }
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

}
