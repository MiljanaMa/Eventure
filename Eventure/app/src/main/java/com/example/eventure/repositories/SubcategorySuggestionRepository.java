package com.example.eventure.repositories;

import com.example.eventure.model.Subcategory;
import com.example.eventure.model.SubcategorySuggestion;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SubcategorySuggestionRepository {

    private final CollectionReference suggestionsCollection;

    public SubcategorySuggestionRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        suggestionsCollection = db.collection("subcategorySuggestions");
    }

    public CompletableFuture<SubcategorySuggestion> getByUID(String uid) {
        CompletableFuture<SubcategorySuggestion> suggestion = new CompletableFuture<>();
        DocumentReference suggestionRef = suggestionsCollection.document(uid);
        suggestionRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        suggestion.complete(document.toObject(SubcategorySuggestion.class));
                    } else {
                        suggestion.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    suggestion.complete(null);});
        return suggestion;
    }

    public CompletableFuture<List<SubcategorySuggestion>> getAll() {
        CompletableFuture<List<SubcategorySuggestion>> allSuggestions = new CompletableFuture<>();
        suggestionsCollection.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<SubcategorySuggestion> suggestions = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        SubcategorySuggestion suggestion = document.toObject(SubcategorySuggestion.class);
                        suggestions.add(suggestion);
                    }
                    allSuggestions.complete(suggestions);
                })
                .addOnFailureListener(e -> {
                    allSuggestions.complete(null);
                });
        return allSuggestions;
    }

    public CompletableFuture<List<SubcategorySuggestion>> getAllPending() {
        CompletableFuture<List<SubcategorySuggestion>> pendingSuggestions = new CompletableFuture<>();
        suggestionsCollection.whereEqualTo("status", "PENDING")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<SubcategorySuggestion> pending = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        SubcategorySuggestion suggestion = document.toObject(SubcategorySuggestion.class);
                        pending.add(suggestion);
                    }
                    pendingSuggestions.complete(pending);
                })
                .addOnFailureListener(e -> {
                    pendingSuggestions.complete(null);
                });
        return pendingSuggestions;
    }
    
    public CompletableFuture<Boolean> create(SubcategorySuggestion newSuggestion) {
        CompletableFuture<Boolean> creationResult = new CompletableFuture<>();
        newSuggestion.setId(UUID.randomUUID().toString());
        suggestionsCollection.document(newSuggestion.getId())
                .set(newSuggestion)
                .addOnSuccessListener(aVoid -> {creationResult.complete(true);})
                .addOnFailureListener(e -> {creationResult.complete(false);});
        return creationResult;
    }


    public CompletableFuture<Boolean> update(SubcategorySuggestion updatedSuggestion) {
        CompletableFuture<Boolean> updateResult = new CompletableFuture<>();
        suggestionsCollection.document(updatedSuggestion.getId())
                .set(updatedSuggestion)
                .addOnSuccessListener(aVoid -> {updateResult.complete(true);})
                .addOnFailureListener(e -> {updateResult.complete(false);});
        return updateResult;
    }

}
