package com.example.eventure.fragments.common;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.model.Company;
import com.example.eventure.model.EmployeeWorkingHours;
import com.example.eventure.model.Image;
import com.example.eventure.model.OwnerRegistrationRequest;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.UserRole;
import com.example.eventure.repositories.ImageRepository;
import com.example.eventure.repositories.OwnerRegistrationRequestRepository;
import com.example.eventure.settings.FragmentTransition;
import com.example.eventure.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;


public class RegisterOwnerFragment1 extends Fragment {

    private static final int GET_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Uri imageUri;
    private OwnerRegistrationRequestRepository requestRepository;

    public RegisterOwnerFragment1() {

    }

    public static RegisterOwnerFragment1 newInstance(String param1, String param2) {
        RegisterOwnerFragment1 fragment = new RegisterOwnerFragment1();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_owner_fragment1, container, false);

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

        Button btnNext  = view.findViewById(R.id.next_button);
        btnNext.setOnClickListener(new View.OnClickListener(){
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

                ImageRepository imageRepository = new ImageRepository();
                imageRepository.create(image).thenAccept(imageAdded ->{
                    if(imageAdded){
                        User owner = new User("", firstName, lastName, email, password, phone, address, image.getId(), UserRole.OWNER, true, "", new ArrayList<EmployeeWorkingHours>());
                        OwnerRegistrationRequest request = new OwnerRegistrationRequest(email, owner);

                        RegisterOwnerFragment2 registerOwnerFragment2 = RegisterOwnerFragment2.newInstance(request);
                        FragmentTransition.to(registerOwnerFragment2, getActivity(),
                                true, R.id.register_owner_container1);
                    }else{
                        Toast.makeText(view.getContext(), "Failed to add image to database.", Toast.LENGTH_SHORT).show();
                    }
                });


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
}