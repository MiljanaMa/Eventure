package com.example.eventure.repositories;

import com.example.eventure.model.Category;
import com.example.eventure.model.EventType;
import com.example.eventure.model.OwnerRegistrationRequest;
import com.example.eventure.model.Subcategory;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CategoryRepository {

    private final CollectionReference categoriesCollection;

    public CategoryRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        categoriesCollection = db.collection("categories");
    }

    public CompletableFuture<Category> getByUID(String uid) {
        CompletableFuture<Category> category = new CompletableFuture<>();
        DocumentReference categoryRef = categoriesCollection.document(uid);
        categoryRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        category.complete(document.toObject(Category.class));
                    } else {
                        category.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    category.complete(null);});
        return category;
    }

    public CompletableFuture<List<Category>> getAll() {
        CompletableFuture<List<Category>> allCategories = new CompletableFuture<>();
        categoriesCollection.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Category> categories = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Category category = document.toObject(Category.class);
                        categories.add(category);
                    }
                    allCategories.complete(categories);
                })
                .addOnFailureListener(e -> {
                    allCategories.complete(null);
                });
        return allCategories;
    }

    public CompletableFuture<List<Category>> getAllByIds(List<String> categoryIds) {
        CompletableFuture<List<Category>> allCategories = new CompletableFuture<>();

        if (categoryIds == null || categoryIds.isEmpty()) {
            allCategories.complete(null);
            return allCategories;
        }

        categoriesCollection.whereIn("id", categoryIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Category> categories = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Category category = document.toObject(Category.class);
                        categories.add(category);
                    }
                    allCategories.complete(categories);
                })
                .addOnFailureListener(e -> {
                    allCategories.complete(null);
                });
        return allCategories;
    }

    public CompletableFuture<Boolean> create(Category newCategory) {
        CompletableFuture<Boolean> creationResult = new CompletableFuture<>();
        newCategory.setId(UUID.randomUUID().toString());
        categoriesCollection.document(newCategory.getId())
                .set(newCategory)
                .addOnSuccessListener(aVoid -> {creationResult.complete(true);})
                .addOnFailureListener(e -> {creationResult.complete(false);});
        return creationResult;
    }

    public CompletableFuture<Boolean> update(Category updatedCategory) {
        CompletableFuture<Boolean> updateResult = new CompletableFuture<>();
        categoriesCollection.document(updatedCategory.getId())
                .set(updatedCategory)
                .addOnSuccessListener(aVoid -> {updateResult.complete(true);})
                .addOnFailureListener(e -> {updateResult.complete(false);});
        return updateResult;
    }

    public CompletableFuture<Boolean> delete(String categoryId) {
        CompletableFuture<Boolean> deletionResult = new CompletableFuture<>();
        categoriesCollection.document(categoryId)
                .delete()
                .addOnSuccessListener(aVoid -> deletionResult.complete(true))
                .addOnFailureListener(e -> deletionResult.complete(false));
        return deletionResult;
    }
    public CompletableFuture<List<Category>> getByCompany(List<String> ids) {
        CompletableFuture<List<Category>> futureCategories = new CompletableFuture<>();
        CollectionReference typesCollection = FirebaseFirestore.getInstance().collection("categories");

        typesCollection.whereIn("id", ids).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Category> categories = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Category category = document.toObject(Category.class);
                        if (category != null) {
                            categories.add(category);
                        }
                    }
                    futureCategories.complete(categories);
                })
                .addOnFailureListener(e -> futureCategories.completeExceptionally(e));

        return futureCategories;
    }
}
