package com.example.eventure.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Package implements Parcelable {

    private String id;
    private String name;
    private String description;
    private double discount;
    private String category; //change to Category
    private List<String> subcategories; //change to Subcategory
    private List<Product> products;
    private List<Service> services;
    private List<String> eventTypes;  //change to EventType
    private double price;
    private List<String> images;
    private int reservationDeadlineInDays;
    private int cancellationDeadlineInDays;
    private Boolean visible;
    private Boolean available;
    private Boolean manualConfirmation;
    private List<PackageItem> items;

    public Package() {
    }

    public Package(String id, String name, String description, double discount, Boolean visible, Boolean available,
                   String category, List<String> subcategories, List<Product> products, List<Service> services,
                   List<String> eventTypes, double price, List<String> images, int reservationDeadlineInDays, int cancellationDeadlineInDays,
                   Boolean manualConfirmation) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.discount = discount;
        this.category = category;
        this.subcategories = subcategories;
        this.products = products;
        this.services = services;
        this.eventTypes = eventTypes;
        this.price = price;
        this.images = images;
        this.reservationDeadlineInDays = reservationDeadlineInDays;
        this.cancellationDeadlineInDays = cancellationDeadlineInDays;
        this.visible = visible;
        this.available = available;
        this.manualConfirmation = manualConfirmation;
    }

    public Package(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.discount = in.readDouble();
        this.category = in.readString();
        this.subcategories = in.readArrayList(String.class.getClassLoader());
        this.products = in.readArrayList(Product.class.getClassLoader());
        this.services = in.readArrayList(Service.class.getClassLoader());
        this.eventTypes = in.readArrayList(String.class.getClassLoader());
        this.price = in.readDouble();
        this.images = in.readArrayList(String.class.getClassLoader());
        this.reservationDeadlineInDays = in.readInt();
        this.cancellationDeadlineInDays = in.readInt();
        this.visible = in.readBoolean();
        this.available = in.readBoolean();
        this.manualConfirmation = in.readBoolean();
    }

    public List<PackageItem> getItems() {
        return items;
    }

    public void setItems(List<PackageItem> items) {
        this.items = items;
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

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<String> subcategories) {
        this.subcategories = subcategories;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public List<String> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<String> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
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

    @Override
    public String toString() {
        return "Package{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", discount=" + discount +
                ", visible=" + visible +
                ", available=" + available +
                ", category='" + category + '\'' +
                ", subcategories=" + subcategories +
                ", products=" + products +
                ", services=" + services +
                ", eventTypes=" + eventTypes +
                ", price=" + price +
                ", images=" + images +
                ", reservationDeadlineInDays=" + reservationDeadlineInDays +
                ", cancellationDeadlineInDays=" + cancellationDeadlineInDays +
                ", manualConfirmation=" + manualConfirmation +
                '}';
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(discount);
        dest.writeString(category);
        dest.writeList(subcategories);
        dest.writeList(products);
        dest.writeList(services);
        dest.writeList(eventTypes);
        dest.writeDouble(price);
        dest.writeList(images);
        dest.writeInt(reservationDeadlineInDays);
        dest.writeInt(cancellationDeadlineInDays);
        dest.writeBoolean(available);
        dest.writeBoolean(visible);
        dest.writeBoolean(manualConfirmation);
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
