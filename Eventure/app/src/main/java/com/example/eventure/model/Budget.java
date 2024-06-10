package com.example.eventure.model;

import java.util.List;

public class Budget {
    private String id;
    private String eventId;
    private double maxBudget;
    private double money;
    private List<String> budgetItemsIds;
    private List<BudgetItem> budgetItems;

    public Budget() {
    }

    public Budget(String id, String eventId, double maxBudget, List<String> budgetItemsIds, List<BudgetItem> budgetItems) {
        this.id = id;
        this.eventId = eventId;
        this.maxBudget = maxBudget;
        this.budgetItemsIds = budgetItemsIds;
        this.budgetItems = budgetItems;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public double getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(double maxBudget) {
        this.maxBudget = maxBudget;
    }

    public List<String> getBudgetItemsIds() {
        return budgetItemsIds;
    }


    public void setBudgetItemsIds(List<String> budgetItemsIds) {
        this.budgetItemsIds = budgetItemsIds;
    }

    public List<BudgetItem> getBudgetItems() {
        return budgetItems;
    }

    public void setBudgetItems(List<BudgetItem> budgetItems) {
        this.budgetItems = budgetItems;
    }
}
