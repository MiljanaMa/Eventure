package com.example.eventure.repositories;

import com.example.eventure.model.Message;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MessageRepository {
    private final CollectionReference messagesCollection;

    public MessageRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        messagesCollection = db.collection("messages");
    }

    public CompletableFuture<List<Message>> getAllByRecipient(String recipientId) {
        CompletableFuture<List<Message>> futureMessages = new CompletableFuture<>();
        CollectionReference messagesCollection = FirebaseFirestore.getInstance().collection("messages");

        messagesCollection.whereEqualTo("recipientId", recipientId).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Message> messages = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Message message = document.toObject(Message.class);
                        if (message != null) {
                            messages.add(message);
                        }
                    }
                    futureMessages.complete(messages);
                })
                .addOnFailureListener(e -> futureMessages.completeExceptionally(e));

        return futureMessages;
    }

    public CompletableFuture<Boolean> create(Message newMessage) {
        CompletableFuture<Boolean> creationResult = new CompletableFuture<>();
        newMessage.setId(UUID.randomUUID().toString());
        messagesCollection.document(newMessage.getId())
                .set(newMessage)
                .addOnSuccessListener(aVoid -> {creationResult.complete(true);})
                .addOnFailureListener(e -> {creationResult.complete(false);});
        return creationResult;
    }

    public CompletableFuture<Boolean> update(Message updatedMessage) {
        CompletableFuture<Boolean> updateResult = new CompletableFuture<>();
        messagesCollection.document(updatedMessage.getId())
                .set(updatedMessage)
                .addOnSuccessListener(aVoid -> {updateResult.complete(true);})
                .addOnFailureListener(e -> {updateResult.complete(false);});
        return updateResult;
    }

    public CompletableFuture<Boolean> delete(String messageId) {
        CompletableFuture<Boolean> deletionResult = new CompletableFuture<>();
        messagesCollection.document(messageId)
                .delete()
                .addOnSuccessListener(aVoid -> deletionResult.complete(true))
                .addOnFailureListener(e -> deletionResult.complete(false));
        return deletionResult;
    }

    public CompletableFuture<List<Message>> getByUser(String senderId){
        CompletableFuture<List<Message>> futureMessages = new CompletableFuture<>();
        CollectionReference messagesCollection = FirebaseFirestore.getInstance().collection("messages");

        messagesCollection.whereEqualTo("senderId", senderId).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Message> messages = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Message message = document.toObject(Message.class);
                        if (message != null) {
                            messages.add(message);
                        }
                    }
                    messagesCollection.whereEqualTo("recipientId", senderId)
                            .get()
                            .addOnSuccessListener(guerySnapshot2 -> {
                                for (DocumentSnapshot document : guerySnapshot2.getDocuments()) {
                                    Message message = document.toObject(Message.class);
                                    if (message != null) {
                                        messages.add(message);
                                    }
                                }
                            });

                    futureMessages.complete(messages);
                })
                .addOnFailureListener(e -> futureMessages.completeExceptionally(e));

        return futureMessages;
    }

    public CompletableFuture<List<Message>> getBy2Users(String senderId, String recipientId){
        CompletableFuture<List<Message>> futureMessages = new CompletableFuture<>();
        CollectionReference messagesCollection = FirebaseFirestore.getInstance().collection("messages");

        messagesCollection
                .whereEqualTo("senderId", senderId)
                .whereEqualTo("recipientId", recipientId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Message> messages = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Message message = document.toObject(Message.class);
                        if (message != null) {
                            messages.add(message);
                        }
                    }
                    messagesCollection
                            .whereEqualTo("recipientId", recipientId)
                            .whereEqualTo("senderId", senderId)
                            .get()
                            .addOnSuccessListener(guerySnapshot2 -> {
                                for (DocumentSnapshot document : guerySnapshot2.getDocuments()) {
                                    Message message = document.toObject(Message.class);
                                    if (message != null) {
                                        messages.add(message);
                                    }
                                }
                            });

                    futureMessages.complete(messages);
                })
                .addOnFailureListener(e -> futureMessages.completeExceptionally(e));

        return futureMessages;
    }
}
