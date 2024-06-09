package com.example.eventure.repositories;

import com.example.eventure.model.Category;
import com.example.eventure.model.Subcategory;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SubcategoryRepository {

    private final CollectionReference subcategoriesCollection;

    public SubcategoryRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        subcategoriesCollection = db.collection("subcategories");
    }

    public CompletableFuture<Subcategory> getByUID(String uid) {
        CompletableFuture<Subcategory> subcategory = new CompletableFuture<>();
        DocumentReference subcategoryRef = subcategoriesCollection.document(uid);
        subcategoryRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        subcategory.complete(document.toObject(Subcategory.class));
                    } else {
                        subcategory.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    subcategory.complete(null);});
        return subcategory;
    }

    public CompletableFuture<List<Subcategory>> getAll() {
        CompletableFuture<List<Subcategory>> allSubcategories = new CompletableFuture<>();
        subcategoriesCollection.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Subcategory> subcategories = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Subcategory subcategory = document.toObject(Subcategory.class);
                        subcategories.add(subcategory);
                    }
                    allSubcategories.complete(subcategories);
                })
                .addOnFailureListener(e -> {
                    allSubcategories.complete(null);
                });
        return allSubcategories;
    }

    public CompletableFuture<List<Subcategory>> getAllByIds(List<String> subcategoryIds) {
        CompletableFuture<List<Subcategory>> allSubcategories = new CompletableFuture<>();

        if (subcategoryIds == null || subcategoryIds.isEmpty()) {
            allSubcategories.complete(null);
            return allSubcategories;
        }

        subcategoriesCollection.whereIn("id", subcategoryIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Subcategory> subcategories = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Subcategory subcategory = document.toObject(Subcategory.class);
                        subcategories.add(subcategory);
                    }
                    allSubcategories.complete(subcategories);
                })
                .addOnFailureListener(e -> {
                    allSubcategories.complete(null);
                });
        return allSubcategories;
    }


    public CompletableFuture<List<Subcategory>> getByCategoryId(String categoryId) {
        CompletableFuture<List<Subcategory>> subcategoriesByCategory = new CompletableFuture<>();
        subcategoriesCollection.whereEqualTo("categoryId", categoryId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Subcategory> subcategories = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Subcategory subcategory = document.toObject(Subcategory.class);
                        subcategories.add(subcategory);
                    }
                    subcategoriesByCategory.complete(subcategories);
                })
                .addOnFailureListener(e -> {
                    subcategoriesByCategory.complete(null);
                });
        return subcategoriesByCategory;
    }

    public CompletableFuture<String> create(Subcategory newSubcategory) {
        CompletableFuture<String> creationResult = new CompletableFuture<>();
        newSubcategory.setId(UUID.randomUUID().toString());
        subcategoriesCollection.document(newSubcategory.getId())
                .set(newSubcategory)
                .addOnSuccessListener(aVoid -> {creationResult.complete(newSubcategory.getId());})
                .addOnFailureListener(e -> {creationResult.complete(null);});
        return creationResult;
    }

    public CompletableFuture<Boolean> update(Subcategory updatedSubcategory) {
        CompletableFuture<Boolean> updateResult = new CompletableFuture<>();
        subcategoriesCollection.document(updatedSubcategory.getId())
                .set(updatedSubcategory)
                .addOnSuccessListener(aVoid -> {updateResult.complete(true);})
                .addOnFailureListener(e -> {updateResult.complete(false);});
        return updateResult;
    }

    public CompletableFuture<Boolean> delete(String subcategoryId) {
        CompletableFuture<Boolean> deletionResult = new CompletableFuture<>();
        subcategoriesCollection.document(subcategoryId)
                .delete()
                .addOnSuccessListener(aVoid -> deletionResult.complete(true))
                .addOnFailureListener(e -> deletionResult.complete(false));
        return deletionResult;
    }
}
