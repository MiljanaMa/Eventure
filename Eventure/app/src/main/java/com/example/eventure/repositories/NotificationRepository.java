package com.example.eventure.repositories;

import com.example.eventure.model.Notification;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.NotificationStatus;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NotificationRepository {
    private final CollectionReference notificationCollection;

    public NotificationRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        notificationCollection = db.collection("notifications");
    }


    public CompletableFuture<List<Notification>> getAllByReceiverId(String receiverId) {
        CompletableFuture<List<Notification>> notificationsByReceiverID = new CompletableFuture<>();
        notificationCollection.whereEqualTo("receiverId", receiverId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Notification> notifications = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Notification notification = document.toObject(Notification.class);
                        notifications.add(notification);
                    }
                    notificationsByReceiverID.complete(notifications);
                })
                .addOnFailureListener(e -> {
                    notificationsByReceiverID.complete(null);
                });
        return notificationsByReceiverID;
    }

    public CompletableFuture<List<Notification>> getUnreadByReceiverId(String receiverId) {
        CompletableFuture<List<Notification>> notificationsByReceiverID = new CompletableFuture<>();
        notificationCollection.whereEqualTo("receiverId", receiverId)
                .whereEqualTo("status", NotificationStatus.UNREAD)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Notification> notifications = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Notification notification = document.toObject(Notification.class);
                        notifications.add(notification);
                    }
                    notificationsByReceiverID.complete(notifications);
                })
                .addOnFailureListener(e -> {
                    notificationsByReceiverID.complete(null);
                });
        return notificationsByReceiverID;
    }

    public CompletableFuture<Boolean> create(Notification newNotification) {
        CompletableFuture<Boolean> creationResult = new CompletableFuture<>();
        newNotification.setId(UUID.randomUUID().toString());
        notificationCollection.document(newNotification.getId())
                .set(newNotification)
                .addOnSuccessListener(aVoid -> {creationResult.complete(true);})
                .addOnFailureListener(e -> {creationResult.complete(false);});
        return creationResult;
    }

    public CompletableFuture<Boolean> update(Notification updatedNotification) {
        CompletableFuture<Boolean> updateResult = new CompletableFuture<>();
        notificationCollection.document(updatedNotification.getId())
                .set(updatedNotification)
                .addOnSuccessListener(aVoid -> {updateResult.complete(true);})
                .addOnFailureListener(e -> {updateResult.complete(false);});
        return updateResult;
    }

    public CompletableFuture<Boolean> delete(String notificationId) {
        CompletableFuture<Boolean> deletionResult = new CompletableFuture<>();
        notificationCollection.document(notificationId)
                .delete()
                .addOnSuccessListener(aVoid -> deletionResult.complete(true))
                .addOnFailureListener(e -> deletionResult.complete(false));
        return deletionResult;
    }
}
