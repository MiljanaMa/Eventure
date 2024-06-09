package com.example.eventure.repositories;

import com.example.eventure.model.Category;
import com.example.eventure.model.EventType;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EventTypeRepository {
    private final CollectionReference eventTypesCollection;

    public EventTypeRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        eventTypesCollection = db.collection("eventTypes");
    }

    public CompletableFuture<EventType> getByUID(String uid) {
        CompletableFuture<EventType> eventType = new CompletableFuture<>();
        DocumentReference eventTypeRef = eventTypesCollection.document(uid);
        eventTypeRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        eventType.complete(document.toObject(EventType.class));
                    } else {
                        eventType.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    eventType.complete(null);});
        return eventType;
    }

    public CompletableFuture<List<EventType>> getAll() {
        CompletableFuture<List<EventType>> allEventTypes = new CompletableFuture<>();
        eventTypesCollection.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<EventType> eventTypes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        EventType eventType = document.toObject(EventType.class);
                        eventTypes.add(eventType);
                    }
                    allEventTypes.complete(eventTypes);
                })
                .addOnFailureListener(e -> {
                    allEventTypes.complete(null);
                });
        return allEventTypes;
    }

    public CompletableFuture<List<EventType>> getAllByIds(List<String> eventTypesIds) {
        CompletableFuture<List<EventType>> allEventTypes = new CompletableFuture<>();

        if (eventTypesIds == null || eventTypesIds.isEmpty()) {
            allEventTypes.complete(null);
            return allEventTypes;
        }

        eventTypesCollection.whereIn("id", eventTypesIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<EventType> eventTypes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        EventType eventType = document.toObject(EventType.class);
                        eventTypes.add(eventType);
                    }
                    allEventTypes.complete(eventTypes);
                })
                .addOnFailureListener(e -> {
                    allEventTypes.complete(null);
                });
        return allEventTypes;
    }

    public CompletableFuture<Boolean> create(EventType newEventType) {
        CompletableFuture<Boolean> creationResult = new CompletableFuture<>();
        newEventType.setId(UUID.randomUUID().toString());
        eventTypesCollection.document(newEventType.getId())
                .set(newEventType)
                .addOnSuccessListener(aVoid -> {creationResult.complete(true);})
                .addOnFailureListener(e -> {creationResult.complete(false);});
        return creationResult;
    }

    public CompletableFuture<Boolean> update(EventType updatedEventType) {
        CompletableFuture<Boolean> updateResult = new CompletableFuture<>();
        eventTypesCollection.document(updatedEventType.getId())
                .set(updatedEventType)
                .addOnSuccessListener(aVoid -> {updateResult.complete(true);})
                .addOnFailureListener(e -> {updateResult.complete(false);});
        return updateResult;
    }
    public CompletableFuture<List<EventType>> getByCompany(List<String> ids) {
        CompletableFuture<List<EventType>> futureTypes = new CompletableFuture<>();
        CollectionReference typesCollection = FirebaseFirestore.getInstance().collection("eventTypes");

        typesCollection.whereIn("id", ids).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<EventType> types = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        EventType type = document.toObject(EventType.class);
                        if (type != null) {
                            types.add(type);
                        }
                    }
                    futureTypes.complete(types);
                })
                .addOnFailureListener(e -> futureTypes.completeExceptionally(e));

        return futureTypes;
    }
}
