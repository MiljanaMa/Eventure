package com.example.eventure.model;

import com.example.eventure.model.enums.OfferType;

public class BudgetItem {
    private String id;
    private String budgetId;
    private String offerId; //change later to String when ids of service/product/package are type of string
    private OfferType offerType;
    //private double plannedAmount; //add later mby
    private boolean purchased;
    private boolean reserved;

    public BudgetItem() {
    }

    public BudgetItem(String id, String budgetId, String offerId, OfferType offerType, boolean purchased, boolean reserved) {
        this.id = id;
        this.budgetId = budgetId;
        this.offerId = offerId;
        this.offerType = offerType;
        this.purchased = purchased;
        this.reserved = reserved;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public OfferType getOfferType() {
        return offerType;
    }

    public void setOfferType(OfferType offerType) {
        this.offerType = offerType;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }
}
