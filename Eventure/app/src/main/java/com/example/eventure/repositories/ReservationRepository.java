package com.example.eventure.repositories;

import com.example.eventure.model.Reservation;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ReservationRepository {
    private final CollectionReference reservationCollection;

    public ReservationRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        reservationCollection = db.collection("reservations");
    }

    public CompletableFuture<List<Reservation>> getAllByCompany(String companyId) {
        CompletableFuture<List<Reservation>> futureReservations = new CompletableFuture<>();

        reservationCollection.whereEqualTo("companyId", companyId).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Reservation> reservations = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Reservation reservation = document.toObject(Reservation.class);
                        if (reservation != null) {
                            reservations.add(reservation);
                        }
                    }
                    futureReservations.complete(reservations);
                })
                .addOnFailureListener(e -> futureReservations.completeExceptionally(e));

        return futureReservations;
    }
    public CompletableFuture<List<Reservation>> getByEmployee(String employeeId) {
        CompletableFuture<List<Reservation>> futureReservations = new CompletableFuture<>();
        CollectionReference reservationCollecction = FirebaseFirestore.getInstance().collection("reservations");

        reservationCollecction.whereEqualTo("employeeId", employeeId).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Reservation> reservations = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Reservation reservation = document.toObject(Reservation.class);
                        if (reservation != null) {
                            reservations.add(reservation);
                        }
                    }
                    futureReservations.complete(reservations);
                })
                .addOnFailureListener(e -> futureReservations.complete(null));

        return futureReservations;
    }
    public CompletableFuture<List<Reservation>> getByOrganizer(String organizerId) {
        CompletableFuture<List<Reservation>> futureReservations = new CompletableFuture<>();
        CollectionReference reservationCollecction = FirebaseFirestore.getInstance().collection("reservations");

        reservationCollecction.whereEqualTo("organizerId", organizerId).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Reservation> reservations = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Reservation reservation = document.toObject(Reservation.class);
                        if (reservation != null) {
                            reservations.add(reservation);
                        }
                    }
                    futureReservations.complete(reservations);
                })
                .addOnFailureListener(e -> futureReservations.completeExceptionally(e));

        return futureReservations;
    }
    public CompletableFuture<Reservation> getByUID(String uid) {
        CompletableFuture<Reservation> reservation = new CompletableFuture<>();
        DocumentReference reservationRef = reservationCollection.document(uid);
        reservationRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        reservation.complete(document.toObject(Reservation.class));
                    } else {
                        reservation.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    reservation.complete(null);});
        return reservation;
    }
    public CompletableFuture<Boolean> update(Reservation updatedReservation) {
        CompletableFuture<Boolean> updateResult = new CompletableFuture<>();
        reservationCollection.document(updatedReservation.getId())
                .set(updatedReservation)
                .addOnSuccessListener(aVoid -> {updateResult.complete(true);})
                .addOnFailureListener(e -> {updateResult.complete(false);});
        return updateResult;
    }
    public CompletableFuture<List<Reservation>> getOrganizerRealized(String organizerId) {
        CompletableFuture<List<Reservation>> futureReservations = new CompletableFuture<>();
        CollectionReference reservationCollecction = FirebaseFirestore.getInstance().collection("reservations");

        reservationCollecction.whereEqualTo("organizerId", organizerId)
                .whereEqualTo("status", "REALIZED")
                .whereEqualTo("isNotified", false).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Reservation> reservations = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Reservation reservation = document.toObject(Reservation.class);
                        if (reservation != null) {
                            reservations.add(reservation);
                        }
                    }
                    futureReservations.complete(reservations);
                })
                .addOnFailureListener(e -> futureReservations.completeExceptionally(e));

        return futureReservations;
    }
    public CompletableFuture<List<Reservation>> getOrganizerRealizedAndCancelled(String organizerId) {
        CompletableFuture<List<Reservation>> futureReservations = new CompletableFuture<>();

        reservationCollection.whereEqualTo("organizerId", organizerId)
                .whereIn("status", Arrays.asList("REALIZED", "CANCELED_BY_PUP")).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Reservation> reservations = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Reservation reservation = document.toObject(Reservation.class);
                        if (reservation != null) {
                            reservations.add(reservation);
                        }
                    }
                    futureReservations.complete(reservations);
                })
                .addOnFailureListener(e -> futureReservations.completeExceptionally(e));

        return futureReservations;
    }

    public CompletableFuture<Boolean> create(Reservation newReservation) {
        CompletableFuture<Boolean> creationResult = new CompletableFuture<>();
        newReservation.setId(UUID.randomUUID().toString());
        reservationCollection.document(newReservation.getId())
                .set(newReservation)
                .addOnSuccessListener(aVoid -> {creationResult.complete(true);})
                .addOnFailureListener(e -> {creationResult.complete(false);});
        return creationResult;
    }

}
