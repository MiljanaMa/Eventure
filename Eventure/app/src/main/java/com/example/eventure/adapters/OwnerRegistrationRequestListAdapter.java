package com.example.eventure.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.fragments.admin.OwnerRegistrationRequestsFragment;
import com.example.eventure.fragments.admin.dialogs.RegistrationRequestDetailsFragment;
import com.example.eventure.fragments.admin.dialogs.RejectRegistrationRequestFragment;
import com.example.eventure.model.Company;
import com.example.eventure.model.EmployeeWorkingHours;
import com.example.eventure.model.Image;
import com.example.eventure.model.OwnerRegistrationRequest;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.ApprovalStatus;
import com.example.eventure.repositories.AuthRepository;
import com.example.eventure.repositories.CompanyRepository;
import com.example.eventure.repositories.ImageRepository;
import com.example.eventure.repositories.OwnerRegistrationRequestRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.utils.ImageUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class OwnerRegistrationRequestListAdapter extends RecyclerView.Adapter<OwnerRegistrationRequestListAdapter.OwnerRegistrationRequestViewHolder>{
    private List<OwnerRegistrationRequest> requests;
    private FragmentManager fragmentManager;
    private OwnerRegistrationRequestsFragment requestFragment;

    public OwnerRegistrationRequestListAdapter(List<OwnerRegistrationRequest> requests, FragmentManager fragmentManager, OwnerRegistrationRequestsFragment requestFragment) {
        this.requests = requests;
        this.fragmentManager = fragmentManager;
        this.requestFragment = requestFragment;
    }

    @NonNull
    @Override
    public OwnerRegistrationRequestListAdapter.OwnerRegistrationRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_registration_request_card, parent, false);
        return new OwnerRegistrationRequestListAdapter.OwnerRegistrationRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnerRegistrationRequestListAdapter.OwnerRegistrationRequestViewHolder holder, int position) {
        OwnerRegistrationRequest request = requests.get(position);
        ImageRepository imageRepository = new ImageRepository();
        imageRepository.getByUID(request.getOwner().getProfileImageId()).thenAccept(imageFromDB -> {
            if (imageFromDB != null) {
                Bitmap bitmap = ImageUtil.decodeBase64ToImage(imageFromDB.getImage());
                holder.ownerImage.setImageBitmap(bitmap);
            }else{
                holder.ownerImage.setImageResource(R.drawable.upload_image);
            }
        });

        String fullName = request.getOwner().getFirstName() + " " +request.getOwner().getLastName();
        holder.ownerFullName.setText(fullName);
        holder.companyName.setText(request.getCompany().getName());
        holder.submissionDate.setText(request.getSubmissionDate().toString());

        holder.requestCard.setTag(request);
        holder.requestCard.setOnClickListener(v -> {
            OwnerRegistrationRequest selectedRequest = (OwnerRegistrationRequest) v.getTag();
            RegistrationRequestDetailsFragment dialogFragment = RegistrationRequestDetailsFragment.newInstance(selectedRequest);
            dialogFragment.show(fragmentManager, "RegistrationRequestDetailsFragment");
        });

        holder.rejectButton.setTag(request);
        holder.rejectButton.setOnClickListener(v -> {
            OwnerRegistrationRequest selectedRequest = (OwnerRegistrationRequest) v.getTag();
            RejectRegistrationRequestFragment dialogFragment = RejectRegistrationRequestFragment.newInstance(selectedRequest, requestFragment);
            dialogFragment.show(fragmentManager, "RejectRegistrationRequestFragment");
        });

        holder.acceptButton.setTag(request);
        holder.acceptButton.setOnClickListener(v -> {
            AuthRepository authRepository = new AuthRepository();
            UserRepository userRepository = new UserRepository();
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            User newUser = new User(request.getOwner());
            Company company = new Company( request.getCompany());

            authRepository.register(newUser.getEmail(), newUser.getPassword()).thenAccept(registerSuccessful -> {
                if (registerSuccessful) {
                    FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                    newUser.setId(currUser.getUid());
                    userRepository.create(newUser).thenAccept(userAdded -> {
                            if (userAdded) {
                                authRepository.sendEmailVerification(currUser).thenAccept(emailSent -> {
                                if(emailSent) {
                                    company.setOwnerId(currUser.getUid());
                                    CompanyRepository companyRepository = new CompanyRepository();
                                    companyRepository.create(company).thenAccept(companyAdded -> {
                                        if(companyAdded != null){
                                            userRepository.getByEmail(newUser.getEmail()).thenAccept(newlyAddedOwner -> {
                                                if( newlyAddedOwner != null){
                                                    newlyAddedOwner.setCompanyId(companyAdded.getId());
                                                    userRepository.update(newlyAddedOwner).thenAccept(userUpdated -> {
                                                        if(userUpdated) {
                                                            userRepository.getCurrentUser().thenAccept(currentUser1 -> {
                                                                if (currentUser1 != null) {
                                                                    authRepository.login(currentUser1.getEmail(), currentUser1.getPassword()).thenAccept(loginSuccessful -> {
                                                                        if (loginSuccessful) {
                                                                            OwnerRegistrationRequestRepository requestRepository = new OwnerRegistrationRequestRepository();
                                                                            request.setStatus(ApprovalStatus.APPROVED);
                                                                            requestRepository.update(request).thenAccept(requestUpdated -> {
                                                                                if (requestUpdated) {
                                                                                    Toast.makeText(v.getContext(), "Registration request is accepted", Toast.LENGTH_SHORT).show();
                                                                                    requestFragment.loadRequests();
                                                                                } else {
                                                                                    Toast.makeText(v.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                            requestFragment.loadRequests();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }else{
                                                            Toast.makeText(v.getContext(), "Company Id is not updated in owner data.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                        }else{
                                            Toast.makeText(v.getContext(), "Company is not created.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    Toast.makeText(v.getContext(), "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                }
                                });
                            } else {
                                Toast.makeText(v.getContext(), "Failed to register owner.", Toast.LENGTH_SHORT).show();
                            }
                        });
                } else {
                    Toast.makeText(v.getContext(), "User is already in auth", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class OwnerRegistrationRequestViewHolder extends RecyclerView.ViewHolder {
        ImageView ownerImage;
        TextView ownerFullName;
        TextView companyName;
        TextView submissionDate;
        Button acceptButton;
        Button rejectButton;
        LinearLayout requestCard;
        public OwnerRegistrationRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            ownerImage = itemView.findViewById(R.id.owner_image);
            ownerFullName = itemView.findViewById(R.id.request_user_full_name);
            companyName = itemView.findViewById(R.id.request_company_name);
            submissionDate = itemView.findViewById(R.id.request_date);
            acceptButton = itemView.findViewById(R.id.accept_request_button);
            rejectButton = itemView.findViewById(R.id.reject_request_button);
            requestCard = itemView.findViewById(R.id.request_card);
        }
    }
}
