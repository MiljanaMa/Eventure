package com.example.eventure.fragments.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.model.EmployeeWorkingHours;
import com.example.eventure.model.Image;
import com.example.eventure.model.OwnerRegistrationRequest;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.UserRole;
import com.example.eventure.repositories.AuthRepository;
import com.example.eventure.repositories.ImageRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.settings.FragmentTransition;
import com.example.eventure.utils.ImageUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RegisterOrganizerFragment extends Fragment {

    private static final int GET_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Uri imageUri;
    Button btnRegisterOrganizer;
    AuthRepository authRepository;
    UserRepository userRepository;
    ImageRepository imageRepository;

    public RegisterOrganizerFragment() {

    }

    public static RegisterOrganizerFragment newInstance() {
        RegisterOrganizerFragment fragment = new RegisterOrganizerFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.register_organizer_fragment, container, false);
        authRepository = new AuthRepository();
        userRepository = new UserRepository();
        imageRepository = new ImageRepository();

        btnRegisterOrganizer = view.findViewById(R.id.register_button);
        btnRegisterOrganizer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = ((EditText) view.findViewById(R.id.email_edit)).getText().toString();
                String password = ((EditText) view.findViewById(R.id.password_edit)).getText().toString();
                String passwordCheck = ((EditText) view.findViewById(R.id.password_check_edit)).getText().toString();
                String firstName = ((EditText) view.findViewById(R.id.first_name_edit)).getText().toString();
                String lastName = ((EditText) view.findViewById(R.id.last_name_edit)).getText().toString();
                String phone = ((EditText) view.findViewById(R.id.phone_edit)).getText().toString();
                String address = ((EditText) view.findViewById(R.id.address_edit)).getText().toString();
                String profileImageString = imageUri != null ? ImageUtil.encodeImageToBase64(imageUri, view.getContext()) : "";

                if (validateInput(email, password, passwordCheck, firstName, lastName, phone, address, profileImageString, view))
                    return;

                Image image = new Image("profile_", profileImageString);

                imageRepository.create(image).thenAccept(imageAdded ->{
                    if(imageAdded){
                        authRepository.register(email, password)
                                .thenAccept(registerSuccessful -> {
                                    if (registerSuccessful) {
                                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                        if(currentUser != null) {
                                            User newUser = new User(currentUser.getUid(), firstName, lastName, email, password, phone, address, image.getId(), UserRole.ORGANIZER, true, "", new ArrayList<EmployeeWorkingHours>());
                                            createUser(newUser, currentUser, view);
                                        }
                                    } else {
                                        Toast.makeText(view.getContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else{
                        Toast.makeText(view.getContext(), "Failed to add image to database.", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        Button btnUploadImage  = view.findViewById(R.id.upload_image_button);
        imageView = view.findViewById(R.id.imageView);

        btnUploadImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GET_IMAGE_REQUEST);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            }
        }
    }

    private static boolean validateInput(String email, String password, String passwordCheck, String firstName, String lastName, String phone, String address, String profileImage, View view) {
        if (email.isEmpty() || password.isEmpty() || passwordCheck.isEmpty() ||
                firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() ||
                address.isEmpty() || profileImage.isEmpty()) {
            Toast.makeText(view.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(view.getContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (password.length() < 6) {
            Toast.makeText(view.getContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (!password.equals(passwordCheck)) {
            Toast.makeText(view.getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void createUser(User newUser, FirebaseUser currentUser, View view) {
        userRepository.create(newUser)
                .thenAccept(userAddedSuccessful -> {
                    if (userAddedSuccessful) {
                        authRepository.sendEmailVerification(currentUser)
                                .thenAccept(emailSentSuccessful -> {
                                    if(emailSentSuccessful) {
                                        Toast.makeText(view.getContext(), "User registered successfully. Please verify your email address.", Toast.LENGTH_SHORT).show();
                                        NavHostFragment.findNavController(RegisterOrganizerFragment.this)
                                                .navigate(R.id.action_registerOrganizerFragment_to_loginFragment);
                                    }else{
                                        Toast.makeText(view.getContext(), "Failed to send email verification.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(view.getContext(), "Failed to add user to database.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
