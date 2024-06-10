package com.example.eventure.repositories;

import android.util.Log;

import com.example.eventure.model.Event;
import com.example.eventure.model.Subcategory;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EventRepository {
    private CollectionReference eventCollection;
    private static FirebaseFirestore db;

    public EventRepository(){
        db = FirebaseFirestore.getInstance();
        eventCollection = db.collection("events");

    }

    public CompletableFuture<Event> getByUID(String uid) {
        CompletableFuture<Event> event = new CompletableFuture<>();
        DocumentReference eventRef = eventCollection.document(uid);
        eventRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        event.complete(document.toObject(Event.class));
                    } else {
                        event.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    event.complete(null);});
        return event;
    }

    public CompletableFuture<List<Event>> getAll() {
        CompletableFuture<List<Event>> allEvents = new CompletableFuture<>();
        eventCollection.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Event> events = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Event event = document.toObject(Event.class);
                        events.add(event);
                        Log.d("\n\nadded event ************************** : ", event.getName());

                    }
                    allEvents.complete(events);
                })
                .addOnFailureListener(e -> {
                    allEvents.complete(null);
                });
        return allEvents;
    }


    public CompletableFuture<List<Event>> getByOrganizerId(String organizerId) {
        CompletableFuture<List<Event>> eventsByCategory = new CompletableFuture<>();
        eventCollection.whereEqualTo("organizerId", organizerId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Event> events = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Event event = document.toObject(Event.class);
                        events.add(event);
                    }
                    eventsByCategory.complete(events);
                })
                .addOnFailureListener(e -> {
                    eventsByCategory.complete(null);
                });
        return eventsByCategory;
    }


}
