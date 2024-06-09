package com.example.eventure.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Product implements Parcelable {

    private String id;
    private String categoryId;
    private String subcategoryId;
    private String name;
    private String description;
    private double price;
    private double discount;
    private double priceWithDiscount;
    private List<String> imagesIds;
    private List<String> eventTypesIds;
    private Boolean available;
    private Boolean visible;

    public Product() {
    }

    public Product(String id, String categoryId, String subcategoryId, String name, String description, double price, double discount,
                   double priceWithDiscount, List<String> imagesIds, List<String> eventTypesIds, Boolean available, Boolean visible) {
        this.id = id;
        this.categoryId = categoryId;
        this.subcategoryId = subcategoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.priceWithDiscount = priceWithDiscount;
        this.imagesIds = imagesIds;
        this.eventTypesIds = eventTypesIds;
        this.available = available;
        this.visible = visible;
    }

    public Product(Parcel in) {
        this.id = in.readString();
        this.categoryId = in.readString();
        this.subcategoryId = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.price = in.readDouble();
        this.discount = in.readDouble();
        this.priceWithDiscount = in.readDouble();
        this.imagesIds = in.readArrayList(String.class.getClassLoader());
        this.eventTypesIds = in.readArrayList(String.class.getClassLoader());
        byte availableVal = in.readByte();
        available = availableVal == 0x02 ? null : availableVal != 0x00;
        byte visibleVal = in.readByte();
        visible = visibleVal == 0x02 ? null : visibleVal != 0x00;

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getPriceWithDiscount() {
        return priceWithDiscount;
    }

    public void setPriceWithDiscount(double priceWithDiscount) {
        this.priceWithDiscount = priceWithDiscount;
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
        return "Product{" +
                "id=" + id +
                ", category='" + categoryId + '\'' +
                ", subcategory='" + subcategoryId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", priceWithDiscount=" + priceWithDiscount +
                ", images=" + imagesIds +
                ", eventType='" + eventTypesIds + '\'' +
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
        dest.writeDouble(price);
        dest.writeDouble(discount);
        dest.writeDouble(priceWithDiscount);
        dest.writeList(imagesIds);
        dest.writeList(eventTypesIds);
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

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
