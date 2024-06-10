package com.example.eventure.repositories;

import com.example.eventure.model.Budget;
import com.example.eventure.model.BudgetItem;
import com.example.eventure.model.Rating;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BudgetRepository {
    private final CollectionReference budgetCollection;

    public BudgetRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        budgetCollection = db.collection("budgets");
    }
    public CompletableFuture<Boolean> update(Budget updatedBudget) {
        CompletableFuture<Boolean> updateResult = new CompletableFuture<>();
        budgetCollection.document(updatedBudget.getId())
                .set(updatedBudget)
                .addOnSuccessListener(aVoid -> {updateResult.complete(true);})
                .addOnFailureListener(e -> {updateResult.complete(false);});
        return updateResult;
    }
    public CompletableFuture<Budget> getByUID(String uid) {
        CompletableFuture<Budget> budget = new CompletableFuture<>();
        DocumentReference budgetRef = budgetCollection.document(uid);
        budgetRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        budget.complete(document.toObject(Budget.class));
                    } else {
                        budget.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    budget.complete(null);});
        return budget;
    }
}
