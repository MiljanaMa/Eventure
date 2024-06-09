package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.fragments.admin.dialogs.DenyReportDialog;
import com.example.eventure.fragments.common.Ratings;
import com.example.eventure.fragments.organizer.dialogs.ReportDialog;
import com.example.eventure.model.CurrentUser;
import com.example.eventure.model.Rating;
import com.example.eventure.model.enums.UserRole;
import com.example.eventure.repositories.AuthRepository;
import com.example.eventure.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {
    private List<Rating> ratings;
    private FragmentManager fragmentManager;
    private Ratings ratingsView;

    public RatingAdapter(List<Rating> ratings, FragmentManager fragmentManager, Ratings ratingsView) {
        this.ratings = ratings;
        this.fragmentManager = fragmentManager;
        this.ratingsView = ratingsView;
    }

    @NonNull
    @Override
    public RatingAdapter.RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //fix here
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_card, parent, false);
        return new RatingAdapter.RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingAdapter.RatingViewHolder holder, int position) {
        Rating rating = ratings.get(position);
        holder.bind(rating, fragmentManager, ratingsView);
    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    public static class RatingViewHolder extends RecyclerView.ViewHolder {
        private FirebaseUser currentUser;
        private UserRepository userRepository;
        private TextView textViewName;
        private TextView textViewComment;
        private TextView textViewRating;
        private TextView textViewDate;
        private Button reportButton;

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            userRepository = new UserRepository();
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            textViewName = (TextView) itemView.findViewById(R.id.textViewNameRating);
            textViewRating = (TextView) itemView.findViewById(R.id.textViewRating);
            textViewComment = (TextView) itemView.findViewById(R.id.textViewCommentRating);
            textViewDate = (TextView) itemView.findViewById(R.id.textViewDateRating);
            reportButton = (Button) itemView.findViewById(R.id.reportButton);
        }

        public void bind(Rating rating, FragmentManager fragmentManager, Ratings ratingsView) {
            userRepository.getByUID(rating.getUserId()).thenAccept(user -> {
                if (user != null) {
                    textViewName.setText(user.getFirstName() + " " + user.getLastName());
                    textViewRating.setText(String.valueOf(rating.getRating()));
                    textViewComment.setText(rating.getComment());
                    Date createdOn = rating.getCreatedOn();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    String formattedDate = sdf.format(createdOn);

                    textViewDate.setText(formattedDate);
                    userRepository.getByUID(currentUser.getUid()).thenAccept(user1 -> {
                        if (user1 != null) {
                            reportButton.setVisibility(user1.getRole().equals(UserRole.OWNER) ? View.VISIBLE : View.GONE);
                            if (user1.getRole().equals(UserRole.OWNER) && rating.isReported()) {
                                reportButton.setVisibility(View.GONE);
                            } else {
                                reportButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ReportDialog dialogFragment = ReportDialog.newInstance(rating.getId(), "rating", ratingsView);
                                        dialogFragment.show(fragmentManager, "ReportDialog");
                                    }
                                });
                            }
                        }
                    });
                }
            });

        }
    }
}
