package com.example.eventure.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventure.model.enums.SubcategoryType;
import com.example.eventure.model.enums.SubcategorySuggestionStatus;
import com.example.eventure.model.enums.UserRole;

public class SubcategorySuggestion implements Parcelable{

    private String id;
    private String userId;
    private Subcategory subcategory;
    private Product product;
    private Service service;
    private SubcategorySuggestionStatus status;

    public SubcategorySuggestion() {
    }

    public SubcategorySuggestion(String id, String userId, Subcategory subcategory, Product product, Service service, SubcategorySuggestionStatus status) {
        this.id = id;
        this.userId = userId;
        this.subcategory = subcategory;
        this.product = product;
        this.service = service;
        this.status = status;
    }

    public SubcategorySuggestion(Parcel in) {
        this.id = in.readString();
        this.userId = in.readString();
        this.subcategory = in.readParcelable(Subcategory.class.getClassLoader());
        this.product = in.readParcelable(Product.class.getClassLoader());
        this.service = in.readParcelable(Service.class.getClassLoader());
        this.status = SubcategorySuggestionStatus.valueOf(in.readString());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Subcategory subcategory) {
        this.subcategory = subcategory;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }


    public SubcategorySuggestionStatus getStatus() {
        return status;
    }

    public void setStatus(SubcategorySuggestionStatus status) {
        this.status = status;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeParcelable(subcategory, 0);
        dest.writeParcelable(product, 0);
        dest.writeParcelable(service, 0);
        if (status != null) {
            dest.writeString(status.name());
        } else {
            dest.writeString(null);
        }
    }

    public static final Parcelable.Creator<SubcategorySuggestion> CREATOR = new Parcelable.Creator<SubcategorySuggestion>() {
        @Override
        public SubcategorySuggestion createFromParcel(Parcel in) {
            return new SubcategorySuggestion(in);
        }

        @Override
        public SubcategorySuggestion[] newArray(int size) {
            return new SubcategorySuggestion[size];
        }
    };
}
