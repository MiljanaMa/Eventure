package com.example.eventure.repositories;

import com.example.eventure.model.Category;
import com.example.eventure.model.Product;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ProductRepository {

    private CollectionReference productCollection;

    public ProductRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        productCollection = db.collection("products");
    }

    public CompletableFuture<List<Product>> getAllByCategoryId(String categoryId) {
        CompletableFuture<List<Product>> productsByCategory = new CompletableFuture<>();
        productCollection.whereEqualTo("categoryId", categoryId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Product product = document.toObject(Product.class);
                        products.add(product);
                    }
                    if (products.isEmpty()) {
                        productsByCategory.complete(null);
                    } else {
                        productsByCategory.complete(products);
                    }
                })
                .addOnFailureListener(e -> {
                    productsByCategory.complete(null);
                });
        return productsByCategory;
    }

    public CompletableFuture<List<Product>> getAllBySubcategoryId(String subcategoryId) {
        CompletableFuture<List<Product>> productsBySubcategory = new CompletableFuture<>();
        productCollection.whereEqualTo("subcategoryId", subcategoryId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Product product = document.toObject(Product.class);
                        products.add(product);
                    }
                    if (products.isEmpty()) {
                        productsBySubcategory.complete(null);
                    } else {
                        productsBySubcategory.complete(products);
                    }
                })
                .addOnFailureListener(e -> {
                    productsBySubcategory.complete(null);
                });
        return productsBySubcategory;
    }

    public CompletableFuture<Boolean> create(Product newProduct) {
        CompletableFuture<Boolean> creationResult = new CompletableFuture<>();
        newProduct.setId(UUID.randomUUID().toString());
        productCollection.document(newProduct.getId())
                .set(newProduct)
                .addOnSuccessListener(aVoid -> {creationResult.complete(true);})
                .addOnFailureListener(e -> {creationResult.complete(false);});
        return creationResult;
    }

}
