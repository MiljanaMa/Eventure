package com.example.eventure.repositories;

import com.example.eventure.fragments.common.Ratings;
import com.example.eventure.model.Category;
import com.example.eventure.model.Company;
import com.example.eventure.model.Rating;
import com.example.eventure.model.Report;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RatingRepository {
    private final CollectionReference ratingCollection;

    public RatingRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ratingCollection = db.collection("ratings");
    }

    public CompletableFuture<List<Rating>> getAllByCompanyId(String companyId) {
        CompletableFuture<List<Rating>> futureRatings = new CompletableFuture<>();
        CollectionReference ratingCollection = FirebaseFirestore.getInstance().collection("ratings");

        ratingCollection.whereEqualTo("companyId", companyId).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Rating> ratings = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Rating rating = document.toObject(Rating.class);
                        if (rating != null) {
                            ratings.add(rating);
                        }
                    }
                    futureRatings.complete(ratings);
                })
                .addOnFailureListener(e -> futureRatings.completeExceptionally(e));

        return futureRatings;
    }
    public CompletableFuture<Boolean> create(Rating newRating) {
        CompletableFuture<Boolean> creationResult = new CompletableFuture<>();
        newRating.setId(UUID.randomUUID().toString());
        ratingCollection.document(newRating.getId())
                .set(newRating)
                .addOnSuccessListener(aVoid -> {creationResult.complete(true);})
                .addOnFailureListener(e -> {creationResult.complete(false);});
        return creationResult;
    }
    public CompletableFuture<Boolean> delete(String ratingId) {
        CompletableFuture<Boolean> deletionResult = new CompletableFuture<>();
        ratingCollection.document(ratingId)
                .delete()
                .addOnSuccessListener(aVoid -> deletionResult.complete(true))
                .addOnFailureListener(e -> deletionResult.complete(false));
        return deletionResult;
    }
    public CompletableFuture<Boolean> update(Rating updateRating) {
        CompletableFuture<Boolean> updateResult = new CompletableFuture<>();
        ratingCollection.document(updateRating.getId())
                .set(updateRating)
                .addOnSuccessListener(aVoid -> {updateResult.complete(true);})
                .addOnFailureListener(e -> {updateResult.complete(false);});
        return updateResult;
    }
    public CompletableFuture<Rating> getByUID(String uid) {
        CompletableFuture<Rating> report = new CompletableFuture<>();
        DocumentReference reportRef = ratingCollection.document(uid);
        reportRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        report.complete(document.toObject(Rating.class));
                    } else {
                        report.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    report.complete(null);});
        return report;
    }
}
