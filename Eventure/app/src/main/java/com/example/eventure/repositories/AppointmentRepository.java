package com.example.eventure.repositories;

import android.util.Log;

import com.example.eventure.model.Appointment;
import com.example.eventure.model.Reservation;
import com.example.eventure.model.User;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AppointmentRepository {
    private CollectionReference appointmentCollection;
    private CollectionReference userCollection;

    public AppointmentRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        appointmentCollection = db.collection("appointments");
        userCollection = db.collection("users");
    }
    public CompletableFuture<Boolean> create(Appointment appointment) {
        CompletableFuture<User> future = new CompletableFuture<>();
        CompletableFuture<Boolean> creationResult = new CompletableFuture<>();

        userCollection.whereEqualTo("id", appointment.getEmployeeId())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        future.complete(document.toObject(User.class));
                        appointment.setEmployeeId(document.getId());
                        appointmentCollection.document(appointment.getId())
                                .set(appointment)
                                .addOnSuccessListener(aVoid -> {creationResult.complete(true);})
                                .addOnFailureListener(e -> {creationResult.complete(false);});

                    } else {
                        future.complete(null); // No matching document found
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Error", e.toString());
                    future.complete(null); // Complete with null in case of failure
                });
        /*CompletableFuture<Boolean> creationResult = new CompletableFuture<>();
        appointmentCollection.document(appointment.getId())
                .set(appointment)
                .addOnSuccessListener(aVoid -> {creationResult.complete(true);})
                .addOnFailureListener(e -> {creationResult.complete(false);});*/
        return creationResult;
    }
    public CompletableFuture<List<Appointment>> getEmployeeAppointments(String employeeId) {
        CompletableFuture<User> future = new CompletableFuture<>();
        CompletableFuture<List<Appointment>> appointments = new CompletableFuture<>();

        userCollection.whereEqualTo("id", employeeId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        future.complete(document.toObject(User.class));
                        appointmentCollection.whereEqualTo("employeeId", document.getId()).get()
                                .addOnSuccessListener(querySnapshot1 -> {
                                    List<Appointment> appointmentList = new ArrayList<>();
                                    for (DocumentSnapshot document1 : querySnapshot1.getDocuments()) {
                                        Appointment appointment = document1.toObject(Appointment.class);
                                        if (appointment != null) {
                                            //getEmployee(appointment.getEmployeeId()).thenAccept(user -> {});
                                            //appointment.setEmployee();
                                            appointmentList.add(appointment);
                                        }
                                    }
                                    appointments.complete(appointmentList);
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure, you can log the error or do other error handling
                                    e.printStackTrace();
                                    appointments.completeExceptionally(e);
                                });

                    } else {
                        future.complete(null); // No matching document found
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Error", e.toString());
                    future.complete(null); // Complete with null in case of failure
                });
        /*CompletableFuture<List<Appointment>> appointments = new CompletableFuture<>();
        appointmentCollection.whereEqualTo("employeeId", employeeId).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Appointment> appointmentList = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Appointment appointment = document.toObject(Appointment.class);
                        if (appointment != null) {
                            //getEmployee(appointment.getEmployeeId()).thenAccept(user -> {});
                            //appointment.setEmployee();
                            appointmentList.add(appointment);
                        }
                    }
                    appointments.complete(appointmentList);
                })
                .addOnFailureListener(e -> {
                    // Handle failure, you can log the error or do other error handling
                    e.printStackTrace();
                    appointments.completeExceptionally(e);
                });*/
        return appointments;
    }
    public CompletableFuture<Boolean> delete(String appointmentId) {
        CompletableFuture<Boolean> deletionResult = new CompletableFuture<>();
        appointmentCollection.document(appointmentId)
                .delete()
                .addOnSuccessListener(aVoid -> deletionResult.complete(true))
                .addOnFailureListener(e -> deletionResult.complete(false));
        return deletionResult;
    }

    public CompletableFuture<List<Appointment>> getByEmployeeAndDate(String employeeId, Date date) {
        CompletableFuture<List<Appointment>> futureAppointments = new CompletableFuture<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        com.example.eventure.model.Date newDate = new com.example.eventure.model.Date(year, month, day);

        appointmentCollection.whereEqualTo("employeeId", employeeId)
                .whereEqualTo("date", newDate).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Appointment> appointments = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Appointment appointment = document.toObject(Appointment.class);
                        if (appointment != null) {
                            appointments.add(appointment);
                        }
                    }
                    futureAppointments.complete(appointments);
                })
                .addOnFailureListener(e -> futureAppointments.complete(null));

        return futureAppointments;
    }
}
