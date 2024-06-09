package com.example.eventure.repositories;

import com.example.eventure.model.Notification;
import com.example.eventure.model.OwnerRegistrationRequest;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.ApprovalStatus;
import com.example.eventure.model.enums.NotificationStatus;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class OwnerRegistrationRequestRepository {
    private final CollectionReference requestsCollection;

    public OwnerRegistrationRequestRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        requestsCollection = db.collection("ownerRegistrationRequests");
    }

    public CompletableFuture<OwnerRegistrationRequest> getByUID(String uid) {
        CompletableFuture<OwnerRegistrationRequest> request = new CompletableFuture<>();
        DocumentReference requestRef = requestsCollection.document(uid);
        requestRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        request.complete(document.toObject(OwnerRegistrationRequest.class));
                    } else {
                        request.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    request.complete(null);});
        return request;
    }

    public CompletableFuture<Boolean> create(OwnerRegistrationRequest newRequest) {
        CompletableFuture<Boolean> creationResult = new CompletableFuture<>();
        newRequest.setId(UUID.randomUUID().toString());
        requestsCollection.document(newRequest.getId())
                .set(newRequest)
                .addOnSuccessListener(aVoid -> {creationResult.complete(true);})
                .addOnFailureListener(e -> {creationResult.complete(false);});
        return creationResult;
    }

    public CompletableFuture<List<OwnerRegistrationRequest>> getAllPending() {
        CompletableFuture<List<OwnerRegistrationRequest>> pendingRequests = new CompletableFuture<>();
        requestsCollection.whereEqualTo("status", ApprovalStatus.PENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<OwnerRegistrationRequest> requests = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        OwnerRegistrationRequest request = document.toObject(OwnerRegistrationRequest.class);
                        requests.add(request);
                    }
                    pendingRequests.complete(requests);
                })
                .addOnFailureListener(e -> {
                    pendingRequests.complete(null);
                });
        return pendingRequests;
    }

    public CompletableFuture<Boolean> update(OwnerRegistrationRequest updatedRequest) {
        CompletableFuture<Boolean> updateResult = new CompletableFuture<>();
        requestsCollection.document(updatedRequest.getId())
                .set(updatedRequest)
                .addOnSuccessListener(aVoid -> {updateResult.complete(true);})
                .addOnFailureListener(e -> {updateResult.complete(false);});
        return updateResult;
    }
}
