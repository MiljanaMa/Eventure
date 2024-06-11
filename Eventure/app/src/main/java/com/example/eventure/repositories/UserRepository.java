package com.example.eventure.repositories;

import android.util.Log;

import com.example.eventure.model.CurrentUser;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.UserRole;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class UserRepository {

    private final CollectionReference userCollection;
    private final CollectionReference currentUserCollection;

    public UserRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userCollection = db.collection("users");
        currentUserCollection = db.collection("currentUser");
    }

    public CompletableFuture<User> getByUID(String uid) {
        CompletableFuture<User> user = new CompletableFuture<>();
        userCollection.whereEqualTo("id", uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Retrieve the first document matching the query
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        user.complete(document.toObject(User.class));
                    } else {
                        user.complete(null); // No matching document found
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Error", e.toString());
                    user.complete(null); // Complete with null in case of failure
                });
        /*DocumentReference userRef = userCollection.document(uid);
        userRef.get()
                .addOnSuccessListener(document -> {
                    if (document != null && document.exists()) {
                        user.complete(document.toObject(User.class));
                    } else {
                        user.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("greska", e.toString());
                    user.complete(null);});*/
        return user;
    }

    public CompletableFuture<User> getByEmail(String email) {
        CompletableFuture<User> user = new CompletableFuture<>();
        userCollection.whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Retrieve the first document matching the query
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        user.complete(document.toObject(User.class));
                    } else {
                        user.complete(null); // No matching document found
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Error", e.toString());
                    user.complete(null); // Complete with null in case of failure
                });
        return user;
    }

    public CompletableFuture<Boolean> create(User newUser) {
        CompletableFuture<Boolean> creationResult = new CompletableFuture<>();
        userCollection.document(newUser.getId())
                .set(newUser)
                .addOnSuccessListener(aVoid -> {
                    creationResult.complete(true);
                })
                .addOnFailureListener(e -> {
                    creationResult.complete(false);
                });
        return creationResult;
    }

    public CompletableFuture<List<User>> getCompanyEmployees(String companyId) {
        CompletableFuture<List<User>> employees = new CompletableFuture<>();
        userCollection.whereEqualTo("companyId", companyId)
                .whereEqualTo("role", "EMPLOYEE").get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> userList = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            userList.add(user);
                        }
                    }
                    employees.complete(userList);
                })
                .addOnFailureListener(e -> {
                    // Handle failure, you can log the error or do other error handling
                    e.printStackTrace();
                    employees.completeExceptionally(e);
                });
        return employees;
    }

    public CompletableFuture<User> updateEmployee(String userId, Map<String, Object> updates) {
        CompletableFuture<User> future = new CompletableFuture<>();
        userCollection.whereEqualTo("id", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        future.complete(document.toObject(User.class));
                        userCollection.document(document.getId()).update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    // Fetch the updated document
                                    userCollection.document(userId).get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                User updatedUser = documentSnapshot.toObject(User.class);
                                                future.complete(updatedUser);
                                            })
                                            .addOnFailureListener(e -> future.completeExceptionally(e));
                                })
                                .addOnFailureListener(e -> future.completeExceptionally(e));

                    } else {
                        future.complete(null); // No matching document found
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Error", e.toString());
                    future.complete(null); // Complete with null in case of failure
                });
        return future;
    }

    public CompletableFuture<CurrentUser> getCurrentUser() {
        CompletableFuture<CurrentUser> user = new CompletableFuture<>();
        DocumentReference userRef = currentUserCollection.document("1");
        userRef.get()
                .addOnSuccessListener(document -> {
                    if (document != null && document.exists()) {
                        user.complete(document.toObject(CurrentUser.class));
                    } else {
                        user.complete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    user.complete(null);
                });
        return user;
    }

    public CompletableFuture<CurrentUser> updateCurrentUser(Map<String, Object> updates) {
        CompletableFuture<CurrentUser> future = new CompletableFuture<>();
        currentUserCollection.document("1").update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Fetch the updated document
                    currentUserCollection.document("1").get()
                            .addOnSuccessListener(documentSnapshot -> {
                                CurrentUser updatedUser = documentSnapshot.toObject(CurrentUser.class);
                                future.complete(updatedUser);
                            })
                            .addOnFailureListener(e -> future.completeExceptionally(e));
                })
                .addOnFailureListener(e -> future.completeExceptionally(e));
        return future;
    }

    public CompletableFuture<Boolean> update(User updatedUser) {
        CompletableFuture<Boolean> updateResult = new CompletableFuture<>();
        userCollection.document(updatedUser.getId())
                .set(updatedUser)
                .addOnSuccessListener(aVoid -> {updateResult.complete(true);})
                .addOnFailureListener(e -> {updateResult.complete(false);});
        return updateResult;
    }

    public CompletableFuture<List<String>> getAdminsIds() {
        CompletableFuture<List<String>> adminIds = new CompletableFuture<>();
        userCollection.whereEqualTo("role", UserRole.ADMIN)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> ids = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        User user = document.toObject(User.class);
                        ids.add(user.getId());
                    }
                    adminIds.complete(ids);
                })
                .addOnFailureListener(e -> {
                    adminIds.complete(null);
                });
        return adminIds;
    }


    public CompletableFuture<User> getCompanyOwner(String companyId) {
        CompletableFuture<User> user = new CompletableFuture<>();
        userCollection.whereEqualTo("companyId", companyId)
                .whereEqualTo("role", "OWNER")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Retrieve the first document matching the query
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        user.complete(document.toObject(User.class));
                    } else {
                        user.complete(null); // No matching document found
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Error", e.toString());
                    user.complete(null); // Complete with null in case of failure
                });
        return user;
    }
    public CompletableFuture<List<User>> getAdmins() {
        CompletableFuture<List<User>> admins = new CompletableFuture<>();
        userCollection.whereEqualTo("role", "ADMIN").get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> userList = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            userList.add(user);
                        }
                    }
                    admins.complete(userList);
                })
                .addOnFailureListener(e -> {
                    // Handle failure, you can log the error or do other error handling
                    e.printStackTrace();
                    admins.completeExceptionally(e);
                });
        return admins;
    }


    public CompletableFuture<List<User>> getByIds(List<String> userIds) {
        CompletableFuture<List<User>> allUsers = new CompletableFuture<>();

        if (userIds == null || userIds.isEmpty()) {
            allUsers.complete(null);
            return allUsers;
        }

        userCollection.whereIn("id", userIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> users = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        User user = document.toObject(User.class);
                        users.add(user);
                    }
                    allUsers.complete(users);
                })
                .addOnFailureListener(e -> {
                    allUsers.complete(null);
                });
        return allUsers;
    }


    public CompletableFuture<List<User>> getAll() {
        CompletableFuture<List<User>> allUsers = new CompletableFuture<>();
        userCollection.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> users = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        User user = document.toObject(User.class);
                        users.add(user);
                    }
                    allUsers.complete(users);
                })
                .addOnFailureListener(e -> {
                    allUsers.complete(null);
                });
        return allUsers;
    }
}
