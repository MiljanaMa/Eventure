package com.example.eventure.fragments.organizer;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.eventure.R;
import com.example.eventure.repositories.ImageRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.utils.ImageUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerMainFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView profileImage;
    private UserRepository userRepository;
    private ImageRepository imageRepository;

    public OrganizerMainFragment() {
        // Required empty public constructor
    }


    public static OrganizerMainFragment newInstance() {
        OrganizerMainFragment fragment = new OrganizerMainFragment();
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
        View view = inflater.inflate(R.layout.organizer_main_fragment, container, false);

        //Just to show example of image decoding
        userRepository = new UserRepository();
        imageRepository = new ImageRepository();
        profileImage = view.findViewById(R.id.profileImage);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userRepository.getByUID(currentUser.getUid()).thenAccept(user -> {
            if(user != null){
                imageRepository.getByUID(user.getProfileImageId()).thenAccept(image -> {
                    if(image != null) {
                        Bitmap bitmap = ImageUtil.decodeBase64ToImage(image.getImage());
                        if (bitmap != null) {
                            profileImage.setImageBitmap(bitmap);
                            Log.d("Profile image", "Profile image exists");
                        }
                    }else{
                        Log.d("No image", "No image");
                    }
                });
            }else{
                Log.d("No document", "No such document");
            }
        });

        return view;
    }

}
