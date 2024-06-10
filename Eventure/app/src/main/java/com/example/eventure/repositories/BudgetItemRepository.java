package com.example.eventure.repositories;

import com.example.eventure.model.BudgetItem;
import com.example.eventure.model.Notification;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BudgetItemRepository {
    private final CollectionReference budgetItemsCollection;

    public BudgetItemRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        budgetItemsCollection = db.collection("budgetItems");
    }

    public CompletableFuture<String> create(BudgetItem newBudgetItem) {
        CompletableFuture<String> creationResult = new CompletableFuture<>();
        newBudgetItem.setId(UUID.randomUUID().toString());
        budgetItemsCollection.document(newBudgetItem.getId())
                .set(newBudgetItem)
                .addOnSuccessListener(aVoid -> {creationResult.complete(newBudgetItem.getId());})
                .addOnFailureListener(e -> {creationResult.complete(null);});
        return creationResult;
    }

}
