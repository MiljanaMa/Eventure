package com.example.eventure.repositories;

import com.example.eventure.model.Image;
import com.example.eventure.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ImageRepository {

    private final CollectionReference imageCollection;

    public ImageRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        imageCollection = db.collection("images");
    }

    public CompletableFuture<Image> getByUID(String uid) {
        CompletableFuture<Image> image = new CompletableFuture<>();
        DocumentReference imageRef = imageCollection.document(uid);
        imageRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        image.complete(document.toObject(Image.class));
                    } else {
                        image.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    image.complete(null);});
        return image;
    }

    public CompletableFuture<Boolean> create(Image newImage) {
        CompletableFuture<Boolean> creationResult = new CompletableFuture<>();
        newImage.setId(newImage.getId() + UUID.randomUUID().toString());
        imageCollection.document(newImage.getId())
                .set(newImage)
                .addOnSuccessListener(aVoid -> {creationResult.complete(true);})
                .addOnFailureListener(e -> {creationResult.complete(false);});
        return creationResult;
    }
}
