package com.example.eventure.fragments.common;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.eventure.R;
import com.example.eventure.fragments.organizer.dialogs.ReportDialog;
import com.example.eventure.model.User;
import com.example.eventure.repositories.ImageRepository;
import com.example.eventure.utils.ImageUtil;

public class OrganizerProfile extends Fragment {

    private static final String ARG_ORGANIZER = "employee";
    private ImageRepository imageRepository;

    private User organizer;

    public OrganizerProfile() {
        // Required empty public constructor
    }

    public static OrganizerProfile newInstance(User organizer) {
        OrganizerProfile fragment = new OrganizerProfile();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ORGANIZER, organizer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            organizer = getArguments().getParcelable(ARG_ORGANIZER);
            // Use the employee object as needed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_profile, container, false);

        imageRepository = new ImageRepository();
        bindOwner(view);
        return view;
    }

    private void bindOwner(View view) {
        ImageView imageView = view.findViewById(R.id.imageViewInfoOrganizer);
        TextView textViewName = view.findViewById(R.id.textViewNameInfoOrganizer);
        TextView textViewEmail = view.findViewById(R.id.textViewEmailInfoOrganizer);
        TextView textViewPhone = view.findViewById(R.id.textViewPhoneInfoOrganizer);
        TextView textViewAddress = view.findViewById(R.id.textViewAddressInfoOrganizer);
        Button report = view.findViewById(R.id.reportOrganizerBtn);

        if (organizer.getProfileImageId() != null) {
            /*Bitmap bitmap = ImageUtil.decodeBase64ToImage(employee.getProfileImage());
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }*/
            imageRepository.getByUID(organizer.getProfileImageId()).thenAccept(image -> {
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
            Glide.with(this).load(organizer.getProfileImageId()).placeholder(R.mipmap.employee).error(R.mipmap.employee).into(imageView);
        }
        textViewName.setText(organizer.getFirstName() + " " + organizer.getLastName());
        textViewEmail.setText(organizer.getEmail());
        textViewPhone.setText(organizer.getPhone());
        textViewAddress.setText(organizer.getAddress());
        Button reportCompany = view.findViewById(R.id.reportCompanyBtn);

        report.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ReportDialog dialogFragment1 = ReportDialog.newInstance(organizer.getId(), "organizer", null);
                dialogFragment1.show(requireActivity().getSupportFragmentManager(), "ReportDialog");
            }
        });
    }
}