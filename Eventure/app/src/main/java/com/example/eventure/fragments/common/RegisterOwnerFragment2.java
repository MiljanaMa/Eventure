package com.example.eventure.fragments.common;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import com.example.eventure.model.Image;
import com.example.eventure.model.OwnerRegistrationRequest;
import com.example.eventure.repositories.ImageRepository;
import com.example.eventure.settings.FragmentTransition;
import com.example.eventure.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;


public class RegisterOwnerFragment2 extends Fragment {

    private static final int GET_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Uri imageUri;
    private static OwnerRegistrationRequest request;

    public RegisterOwnerFragment2() {
    }


    public static RegisterOwnerFragment2 newInstance(OwnerRegistrationRequest registrationRequest) {
        RegisterOwnerFragment2 fragment = new RegisterOwnerFragment2();
        Bundle args = new Bundle();
        args.putParcelable("REQUEST", registrationRequest);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            request = getArguments().getParcelable("REQUEST");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.register_owner_fragment2, container, false);

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
                String name = ((EditText) view.findViewById(R.id.name_edit)).getText().toString();
                String address = ((EditText) view.findViewById(R.id.address_edit)).getText().toString();
                String phone = ((EditText) view.findViewById(R.id.phone_edit)).getText().toString();
                String description = ((EditText) view.findViewById(R.id.description_edit)).getText().toString();
                String imageString = imageUri != null ? ImageUtil.encodeImageToBase64(imageUri, view.getContext()) : "";

                if (validateInput(email, name, phone, address, description, imageString, view))
                    return;

                Image image = new Image("company_", imageString);

                ImageRepository imageRepository = new ImageRepository();
                imageRepository.create(image).thenAccept(imageAdded ->{
                    if(imageAdded){
                        List<String> imagesIds = new ArrayList<String>();
                        imagesIds.add(image.getId());

                        Company company = new Company("", "",email, name, address, phone, description, imagesIds);
                        request.setCompany(company);
                        RegisterOwnerFragment3 registerOwnerFragment3 = RegisterOwnerFragment3.newInstance(request);
                        FragmentTransition.to(registerOwnerFragment3, getActivity(),
                                true, R.id.register_owner_container2);
                    }else{
                        Toast.makeText(view.getContext(), "Failed to add image to database.", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        Button btnBack  = view.findViewById(R.id.back_button);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(String.valueOf(R.id.register_owner_container1), FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

    private static boolean validateInput(String email,  String name, String phone, String address, String description, String profileImage, View view) {
        if (email.isEmpty()  || name.isEmpty() || phone.isEmpty() || address.isEmpty() ||  description.isEmpty() || profileImage.isEmpty()) {
            Toast.makeText(view.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(view.getContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }
}