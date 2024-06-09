package com.example.eventure.repositories;

import com.example.eventure.model.Package;
import com.example.eventure.model.Reservation;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PackageRepository {
    private final CollectionReference prackageCollection;

    public PackageRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        prackageCollection = db.collection("packages");
    }

    public CompletableFuture<Package> getByUID(String uid) {
        CompletableFuture<Package> packageCompletableFuture = new CompletableFuture<>();
        DocumentReference packageRef = prackageCollection.document(uid);
        packageRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        packageCompletableFuture.complete(document.toObject(Package.class));
                    } else {
                        packageCompletableFuture.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    packageCompletableFuture.complete(null);});
        return packageCompletableFuture;
    }
}
