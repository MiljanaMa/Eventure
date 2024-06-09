package com.example.eventure.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventure.model.enums.SubcategoryType;
import com.example.eventure.model.enums.UserRole;

public class Subcategory implements Parcelable {

    private String id;
    private String name;
    private String description;
    private String categoryId;
    private SubcategoryType type;

    public Subcategory() {
    }

    public Subcategory(String id, String name, String description, String categoryId, SubcategoryType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.type = type;
    }

    public Subcategory(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.categoryId = in.readString();
        this.type = SubcategoryType.valueOf(in.readString());;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public SubcategoryType getType() {
        return type;
    }

    public void setType(SubcategoryType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(categoryId);
        if (type != null) {
            dest.writeString(type.name());
        } else {
            dest.writeString(null);
        }
    }

    public static final Parcelable.Creator<Subcategory> CREATOR = new Parcelable.Creator<Subcategory>() {
        @Override
        public Subcategory createFromParcel(Parcel in) {
            return new Subcategory(in);
        }

        @Override
        public Subcategory[] newArray(int size) {
            return new Subcategory[size];
        }
    };
}
