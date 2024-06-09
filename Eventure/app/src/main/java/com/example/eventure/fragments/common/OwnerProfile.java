package com.example.eventure.fragments.common;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.eventure.R;
import com.example.eventure.model.Service;
import com.example.eventure.model.User;
import com.example.eventure.repositories.ImageRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.settings.FragmentTransition;
import com.example.eventure.utils.ImageUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class OwnerProfile extends Fragment {

    private static final String ARG_OWNER = "employee";
    private ImageRepository imageRepository;

    private User owner;

    public OwnerProfile() {
        // Required empty public constructor
    }

    public static OwnerProfile newInstance(User owner) {
        OwnerProfile fragment = new OwnerProfile();
        Bundle args = new Bundle();
        args.putParcelable(ARG_OWNER, owner);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            owner = getArguments().getParcelable(ARG_OWNER);
            // Use the employee object as needed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_owner_profile, container, false);

        imageRepository = new ImageRepository();
        bindOwner(view);
        return view;
    }

    private void bindOwner(View view) {
        ImageView imageView = view.findViewById(R.id.imageViewInfoOwner);
        TextView textViewName = view.findViewById(R.id.textViewNameInfoOwner);
        TextView textViewEmail = view.findViewById(R.id.textViewEmailInfoOwner);
        TextView textViewPhone = view.findViewById(R.id.textViewPhoneInfoOwner);
        TextView textViewAddress = view.findViewById(R.id.textViewAddressInfoOwner);

        if (owner.getProfileImageId() != null) {
            /*Bitmap bitmap = ImageUtil.decodeBase64ToImage(employee.getProfileImage());
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }*/
            imageRepository.getByUID(owner.getProfileImageId()).thenAccept(image -> {
                if (image != null) {
                    Bitmap bitmap = ImageUtil.decodeBase64ToImage(image.getImage());
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        Log.d("Profile image", "Profile image exists");
                    }
                } else {
                    Log.d("No image", "No image");
                }
            });
        } else {
            //set some default image
            Glide.with(this).load(owner.getProfileImageId()).placeholder(R.mipmap.employee).error(R.mipmap.employee).into(imageView);
        }
        textViewName.setText(owner.getFirstName() + " " + owner.getLastName());
        textViewEmail.setText(owner.getEmail());
        textViewPhone.setText(owner.getPhone());
        textViewAddress.setText(owner.getAddress());
    }
}