package com.example.eventure.repositories;

import android.util.Log;

import com.example.eventure.model.Category;
import com.example.eventure.model.Company;
import com.example.eventure.model.Subcategory;
import com.example.eventure.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CompanyRepository {
    private final CollectionReference companyCollection;

    public CompanyRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        companyCollection = db.collection("companies");
    }

    public CompletableFuture<Company> getByUID(String uid) {
        CompletableFuture<Company> company = new CompletableFuture<>();
        DocumentReference companyref = companyCollection.document(uid);
        companyref.get()
                .addOnSuccessListener(document -> {
                    if (document != null && document.exists()) {
                        company.complete(document.toObject(Company.class));
                    } else {
                        company.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    company.complete(null);});
        return company;
    }

    public CompletableFuture<Company> create(Company newCompany) {
        CompletableFuture<Company> creationResult = new CompletableFuture<>();
        newCompany.setId(UUID.randomUUID().toString());
        companyCollection.document(newCompany.getId())
                .set(newCompany)
                .addOnSuccessListener(aVoid -> {creationResult.complete(newCompany);})
                .addOnFailureListener(e -> {creationResult.complete(null);});
        return creationResult;
    }
}
