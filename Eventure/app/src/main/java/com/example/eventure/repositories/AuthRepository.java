package com.example.eventure.repositories;

import com.example.eventure.model.CurrentUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.context.AttributeContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AuthRepository {

    private final FirebaseAuth mAuth;
    private final CollectionReference currentUserCollection;

    public AuthRepository(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserCollection = db.collection("currentUser");
    }

    public CompletableFuture<Boolean> login (String email, String password){
        CompletableFuture<Boolean> loginResult = new CompletableFuture<>();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {loginResult.complete(true);})
                .addOnFailureListener(e -> {loginResult.complete(false);});

        return loginResult;
    }

    public CompletableFuture<Boolean> register (String email, String password){
        CompletableFuture<Boolean> registerResult = new CompletableFuture<>();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(aVoid -> {registerResult.complete(true);})
                .addOnFailureListener(e -> {registerResult.complete(false);});

        return registerResult;
    }

    public CompletableFuture<Boolean> sendEmailVerification(FirebaseUser currentUser) {
        CompletableFuture<Boolean> emailResult = new CompletableFuture<>();
        currentUser.sendEmailVerification()
                .addOnSuccessListener(aVoid -> emailResult.complete(true))
                .addOnFailureListener(e -> emailResult.complete(false));
        return emailResult;
    }
    public CompletableFuture<Boolean> deactivate(String email, String password) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();

        // Sign in the user using their email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    mAuth.getCurrentUser().delete()
                            .addOnSuccessListener(aVoid -> result.complete(true)) // Complete with true if deletion is successful
                            .addOnFailureListener(e -> result.complete(false));
                })
                .addOnFailureListener(e -> result.complete(false));

        return result;
    }
    public CompletableFuture<Boolean> activate(String email, String password) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(aVoid -> {
                    mAuth.getCurrentUser().sendEmailVerification()
                            .addOnSuccessListener(aVoid1 -> result.complete(true))
                            .addOnFailureListener(e -> result.complete(false));})
                .addOnFailureListener(e -> {result.complete(false);});

        return result;
    }
    public CompletableFuture<Boolean> registerEmployee (String email, String password){
        CompletableFuture<Boolean> registerResult = new CompletableFuture<>();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(aVoid -> {
                    mAuth.getCurrentUser().sendEmailVerification()
                        .addOnSuccessListener(aVoid1 -> registerResult.complete(true))
                        .addOnFailureListener(e -> registerResult.complete(false));})
                .addOnFailureListener(e -> {registerResult.complete(false);});

        return registerResult;
    }
}
