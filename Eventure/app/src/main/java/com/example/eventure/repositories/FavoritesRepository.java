package com.example.eventure.repositories;

import com.example.eventure.model.Favorites;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FavoritesRepository {
    private final CollectionReference favoritesCollection;

    public FavoritesRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        favoritesCollection = db.collection("favorites");
    }

    public CompletableFuture<List<Favorites>> getAllByOrganizerId(String organizerId) {
        CompletableFuture<List<Favorites>> futureFavorites = new CompletableFuture<>();
        CollectionReference favoritesCollection = FirebaseFirestore.getInstance().collection("favorites");

        favoritesCollection.whereEqualTo("organizerId", organizerId).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Favorites> favorites = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Favorites fav = document.toObject(Favorites.class);
                        if (fav != null) {
                            favorites.add(fav);
                        }
                    }
                    futureFavorites.complete(favorites);
                })
                .addOnFailureListener(e -> futureFavorites.completeExceptionally(e));

        return futureFavorites;
    }

    public CompletableFuture<Boolean> create(Favorites newFav) {
        CompletableFuture<Boolean> creationResult = new CompletableFuture<>();
        newFav.setId(UUID.randomUUID().toString());
        favoritesCollection.document(newFav.getId())
                .set(newFav)
                .addOnSuccessListener(aVoid -> {creationResult.complete(true);})
                .addOnFailureListener(e -> {creationResult.complete(false);});
        return creationResult;
    }
    public CompletableFuture<Boolean> delete(String favoritesId) {
        CompletableFuture<Boolean> deletionResult = new CompletableFuture<>();
        favoritesCollection.document(favoritesId)
                .delete()
                .addOnSuccessListener(aVoid -> deletionResult.complete(true))
                .addOnFailureListener(e -> deletionResult.complete(false));
        return deletionResult;
    }
}
