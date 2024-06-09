package com.example.eventure.repositories;

import com.example.eventure.model.EventType;
import com.example.eventure.model.Report;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ReportRepository {
    private final CollectionReference reportCollection;

    public ReportRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        reportCollection = db.collection("reports");
    }

    public CompletableFuture<List<Report>> getAll() {
        CompletableFuture<List<Report>> futureReports = new CompletableFuture<>();
        CollectionReference ratingCollection = FirebaseFirestore.getInstance().collection("reports");

        ratingCollection.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Report> reports = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Report report = document.toObject(Report.class);
                        if (report != null) {
                            reports.add(report);
                        }
                    }
                    futureReports.complete(reports);
                })
                .addOnFailureListener(e -> futureReports.completeExceptionally(e));

        return futureReports;
    }
    public CompletableFuture<Boolean> create(Report newReport) {
        CompletableFuture<Boolean> creationResult = new CompletableFuture<>();
        newReport.setId(UUID.randomUUID().toString());
        reportCollection.document(newReport.getId())
                .set(newReport)
                .addOnSuccessListener(aVoid -> {creationResult.complete(true);})
                .addOnFailureListener(e -> {creationResult.complete(false);});
        return creationResult;
    }
    public CompletableFuture<Boolean> update(Report updateReport) {
        CompletableFuture<Boolean> updateResult = new CompletableFuture<>();
        reportCollection.document(updateReport.getId())
                .set(updateReport)
                .addOnSuccessListener(aVoid -> {updateResult.complete(true);})
                .addOnFailureListener(e -> {updateResult.complete(false);});
        return updateResult;
    }
    public CompletableFuture<Report> getByUID(String uid) {
        CompletableFuture<Report> report = new CompletableFuture<>();
        DocumentReference reportRef = reportCollection.document(uid);
        reportRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        report.complete(document.toObject(Report.class));
                    } else {
                        report.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    report.complete(null);});
        return report;
    }
}
