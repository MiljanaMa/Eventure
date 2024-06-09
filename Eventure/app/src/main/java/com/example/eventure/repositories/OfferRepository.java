package com.example.eventure.repositories;

import android.util.Log;

import com.example.eventure.model.Category;
import com.example.eventure.model.Offer;
import com.example.eventure.model.Subcategory;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OfferRepository {
    private CollectionReference offerCollection;
    private CollectionReference categoriesCollection;
    private CollectionReference subcategoriesCollection;
    private static FirebaseFirestore db;

    public OfferRepository(){
        db = FirebaseFirestore.getInstance();
        offerCollection = db.collection("offers");
        categoriesCollection = db.collection("categories");
        subcategoriesCollection = db.collection("subcategories");
    }

    public CompletableFuture<List<Offer>> getAll(){
        CompletableFuture<List<Offer>> allOffers = new CompletableFuture<>();
        offerCollection.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Offer> offers = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Offer offer = document.toObject(Offer.class);
                        String categoryId = offer.getCategoryId();
                        String subcategoryId = offer.getSubcategoryId();
                        CompletableFuture<Category> categoryFuture = getCategoryById(categoryId);
                        CompletableFuture<Subcategory> subcategoryFuture = getSubcategoryById(subcategoryId);

                        //offers.add(offer);
                        CompletableFuture.allOf(categoryFuture, subcategoryFuture)
                                .thenAcceptAsync(voidResult -> {
                                    Category category = categoryFuture.join();
                                    Subcategory subcategory = subcategoryFuture.join();

                                    Log.d("category name", category.getName());
                                    offer.setCategory(category);
                                    offer.setSubcategory(subcategory);

                                    //offers.add(offer);
                                });
                        offers.add(offer);
                        Log.d("pffers", offers.toString());

                    }

                    allOffers.complete(offers);
                })
                .addOnFailureListener(e -> {
                    allOffers.complete(null);
                });
        return allOffers;
    } //prikaze offers ali ne poveze


    /*public CompletableFuture<List<Offer>> getAll() {
        CompletableFuture<List<Offer>> allOffersFuture = new CompletableFuture<>();
        offerCollection.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Offer> offers = new ArrayList<>();
                    List<CompletableFuture<Void>> futures = new ArrayList<>();

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Offer offer = document.toObject(Offer.class);
                        String categoryId = offer.getCategoryId();
                        String subcategoryId = offer.getSubcategoryId();

                        CompletableFuture<Category> categoryFuture = getCategoryById(categoryId);
                        CompletableFuture<Subcategory> subcategoryFuture = getSubcategoryById(subcategoryId);

                        CompletableFuture<Void> offerProcessingFuture = CompletableFuture.allOf(categoryFuture, subcategoryFuture)
                                .thenApplyAsync(voidResult -> {
                                    Category category = categoryFuture.join();
                                    Subcategory subcategory = subcategoryFuture.join();

                                    offer.setCategory(category);
                                    offer.setSubcategory(subcategory);
                                    Log.d("category name connected", offer.getCategory().getName());
                                    Log.d("category name", category.getName());


                                    return null;
                                });
                        Log.d("futures", futures.toString());
                        futures.add(offerProcessingFuture);
                    }

                    CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                    allFutures.thenApplyAsync(voidResult -> {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Offer offer = document.toObject(Offer.class);
                            Log.d("final petlja aaa", offer.getCategory().getName());

                            offers.add(offer);
                        }
                        return offers;
                    }).thenAccept(allOffers -> allOffersFuture.complete(allOffers));
                })
                .addOnFailureListener(e -> {
                    allOffersFuture.completeExceptionally(e);
                });

        return allOffersFuture;
    } pronadje kategorije ali pre vrati listu futures koja bude prazna, a uspesno dobavi kategpriju i podkategoriju, samo kasnije*/

    public CompletableFuture<Subcategory> getSubcategoryById(String subcategoryId) {
        CompletableFuture<Subcategory> subcategoryFuture = new CompletableFuture<>();
        DocumentReference subcategoryRef = db.collection("subcategories").document(subcategoryId);
        subcategoryRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Subcategory subcategory = documentSnapshot.toObject(Subcategory.class);
                        subcategoryFuture.complete(subcategory);
                    } else {
                        subcategoryFuture.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    subcategoryFuture.completeExceptionally(e);
                });
        return subcategoryFuture;
    }

    public CompletableFuture<Category> getCategoryById(String categoryId) {
        CompletableFuture<Category> categoryFuture = new CompletableFuture<>();
        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference subcategoryRef = db.collection("categories").document(categoryId);
        subcategoryRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Category category = documentSnapshot.toObject(Category.class);
                        categoryFuture.complete(category);
                    } else {
                        categoryFuture.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    categoryFuture.completeExceptionally(e);
                });
        return categoryFuture;
    }

}
