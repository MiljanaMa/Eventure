package com.example.eventure.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.eventure.model.enums.OfferType;

import java.util.List;

public class Offer implements Parcelable {
    private String id;
    private String name;
    private String type; //product, service, package
    private String offerId; // id of product/service/package, identifikuje se preko offerId + offerType
    private String categoryId;
    private Category category;
    private String subcategoryId;
    private Subcategory subcategory;
    private String description;
    private double price;
    private String image;
    private Boolean available;
    private Boolean visible;

    public  Offer(){

    }

    public String  getId() {
        return id;
    }

    public void setId(String  id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(String subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Subcategory subcategory) {
        this.subcategory = subcategory;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    protected Offer(Parcel in) {
        id = in.readString();
        name = in.readString();
        type = in.readString();
        offerId = in.readString();
        categoryId = in.readString();
        subcategoryId = in.readString();
        description = in.readString();
        price = in.readDouble();
        image = in.readString();
        byte availableVal = in.readByte();
        available = availableVal == 0x02 ? null : availableVal != 0x00;
        byte visibleVal = in.readByte();
        visible = visibleVal == 0x02 ? null : visibleVal != 0x00;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(offerId);
        dest.writeString(categoryId);
        dest.writeString(subcategoryId);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeString(image);
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

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Offer> CREATOR = new Parcelable.Creator<Offer>() {
        @Override
        public Offer createFromParcel(Parcel in) {
            return new Offer(in);
        }

        @Override
        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };
}
