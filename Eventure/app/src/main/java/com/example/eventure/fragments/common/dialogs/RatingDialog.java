package com.example.eventure.fragments.common.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.databinding.FragmentRatingDialogBinding;
import com.example.eventure.fragments.common.CompanyProfile;
import com.example.eventure.fragments.common.Ratings;
import com.example.eventure.model.Notification;
import com.example.eventure.model.Rating;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.NotificationStatus;
import com.example.eventure.repositories.CompanyRepository;
import com.example.eventure.repositories.NotificationRepository;
import com.example.eventure.repositories.RatingRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.utils.UUIDUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class RatingDialog extends DialogFragment {
    private static final String ARG_PARAM1 = "param1";
    private String companyId;
    private FragmentRatingDialogBinding binding;
    private Rating rating = new Rating();
    private FirebaseUser currentUser;

    private static Ratings ratingsView;
    private static View companyProfile;

    public RatingDialog() {
        // Required empty public constructor
    }

    public static RatingDialog newInstance(String companyId, Ratings ratings, View view) {
        RatingDialog fragment = new RatingDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, companyId);
        fragment.setArguments(args);
        ratingsView = ratings;
        companyProfile = view;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            companyId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRatingDialogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button btnCancel = root.findViewById(R.id.cancel_button_rating);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button addBtn = root.findViewById(R.id.rate_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.ratingNumber.getText().toString().isEmpty() || binding.ratingComment.getText().toString().isEmpty()) {
                    Toast.makeText(root.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                String ratingText = binding.ratingNumber.getText().toString();
                int ratingNumber = 0;

                try {
                    ratingNumber = Integer.parseInt(ratingText);
                } catch (NumberFormatException e) {
                    Toast.makeText(root.getContext(), "Only numbers are allowed", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                if (ratingNumber < 1 || ratingNumber > 5) {
                    Toast.makeText(root.getContext(), "Only numbers between 1-5", Toast.LENGTH_SHORT).show();
                    return;
                }
                rating.setRating(ratingNumber);
                rating.setComment(binding.ratingComment.getText().toString());
                rating.setCompanyId(companyId);
                rating.setCreatedOn(new Date());
                rating.setReported(false);

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
                rating.setUserId(currentUser.getUid());

                RatingRepository ratingRepository = new RatingRepository();
                NotificationRepository notificationRepository = new NotificationRepository();
                UserRepository userRepository = new UserRepository();
                ratingRepository.create(rating).thenAccept(ratingCreated -> {
                    if (ratingCreated) {
                        Toast.makeText(root.getContext(), "Rating created successfully", Toast.LENGTH_SHORT).show();
                        userRepository.getCompanyOwner(companyId).thenAccept(user -> {
                            if (user != null) {
                                Notification notification = new Notification(UUIDUtil.generateUUID(), "New rating", "Your company has new rating with comment: " + rating.getComment(),
                                        user.getId(), currentUser.getUid(), NotificationStatus.UNREAD);
                                notificationRepository.create(notification).thenAccept(creationResult -> {
                                    Button addRating = companyProfile.findViewById(R.id.addRating);
                                    addRating.setVisibility(View.GONE);
                                    ratingsView.refreshRatings();
                                    dismiss();
                                });
                            }
                        });
                    } else {
                        Toast.makeText(root.getContext(), "Failed to create rating", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return root;
    }
}