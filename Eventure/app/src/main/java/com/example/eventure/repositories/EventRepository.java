package com.example.eventure.repositories;

import android.util.Log;

import com.example.eventure.model.Event;
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


    // TODO: dodaj za update, trebace kada se bude dodavao budzet i agenda i gosti... ili da to sve budu reference pa da se samo dodaju reference kada se update ovo...
    // ili dodati samo metodu update budget/ update agenda/ guest list??


}
