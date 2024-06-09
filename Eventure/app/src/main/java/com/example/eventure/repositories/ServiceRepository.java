package com.example.eventure.repositories;

import com.example.eventure.model.EventType;
import com.example.eventure.model.Product;
import com.example.eventure.model.Service;
import com.example.eventure.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServiceRepository {
    private CollectionReference serviceCollection;

    public ServiceRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        serviceCollection = db.collection("services");
    }
    public CompletableFuture<List<Service>> getServicesByProvider(String providerId) {
        CompletableFuture<List<Service>> futureServices = new CompletableFuture<>();
        serviceCollection.whereArrayContains("serviceProviders", providerId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Service> services = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Service service = document.toObject(Service.class);
                            services.add(service);
                        }
                        futureServices.complete(services); // Complete the CompletableFuture with the list of services
                    } else {
                        futureServices.completeExceptionally(task.getException()); // Complete exceptionally if there's a failure
                    }
                });
        return futureServices;
    }

    public CompletableFuture<List<Service>> getAllByCategoryId(String categoryId) {
        CompletableFuture<List<Service>> servicesByCategory = new CompletableFuture<>();
        serviceCollection.whereEqualTo("categoryId", categoryId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Service> services = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Service service = document.toObject(Service.class);
                        services.add(service);
                    }
                    if (services.isEmpty()) {
                        servicesByCategory.complete(null);
                    } else {
                        servicesByCategory.complete(services);
                    }
                })
                .addOnFailureListener(e -> {
                    servicesByCategory.complete(null);
                });
        return servicesByCategory;
    }

    public CompletableFuture<List<Service>> getAllBySubcategoryId(String subcategoryId) {
        CompletableFuture<List<Service>> servicesBySubategory = new CompletableFuture<>();
        serviceCollection.whereEqualTo("subcategoryId", subcategoryId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Service> services = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Service service = document.toObject(Service.class);
                        services.add(service);
                    }
                    if (services.isEmpty()) {
                        servicesBySubategory.complete(null);
                    } else {
                        servicesBySubategory.complete(services);
                    }
                })
                .addOnFailureListener(e -> {
                    servicesBySubategory.complete(null);
                });
        return servicesBySubategory;
    }
    public CompletableFuture<Service> getByUID(String uid) {
        CompletableFuture<Service> service = new CompletableFuture<>();
        DocumentReference serviceRef = serviceCollection.document(uid);
        serviceRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        service.complete(document.toObject(Service.class));
                    } else {
                        service.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    service.complete(null);});
        return service;
    }
}
