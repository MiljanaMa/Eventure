package com.example.eventure.fragments.admin.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.fragments.admin.CategoryManagmentFragment;
import com.example.eventure.fragments.admin.OwnerRegistrationRequestsFragment;
import com.example.eventure.model.Category;
import com.example.eventure.model.OwnerRegistrationRequest;
import com.example.eventure.model.enums.ApprovalStatus;
import com.example.eventure.repositories.CategoryRepository;
import com.example.eventure.repositories.OwnerRegistrationRequestRepository;
import com.example.eventure.utils.EmailUtil;


public class RejectRegistrationRequestFragment extends DialogFragment {

    private static OwnerRegistrationRequest request;
    private static OwnerRegistrationRequestsFragment requestFragment;

    public RejectRegistrationRequestFragment() {

    }


    public static RejectRegistrationRequestFragment newInstance(OwnerRegistrationRequest selectedRequest, OwnerRegistrationRequestsFragment requestsFragment) {
        RejectRegistrationRequestFragment fragment = new RejectRegistrationRequestFragment();
        Bundle args = new Bundle();
        args.putParcelable("REQUEST", selectedRequest);
        fragment.setArguments(args);
        requestFragment = requestsFragment;
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
        View view =  inflater.inflate(R.layout.reject_registration_request_fragment, container, false);

        Button btnCancel = view.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button btnSubmit = view.findViewById(R.id.submit_button);
        btnSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String rejectionReason =((EditText) view.findViewById(R.id.rejection_reason)).getText().toString();
                request.setRejectionReason(rejectionReason);
                request.setStatus(ApprovalStatus.REJECTED);

                OwnerRegistrationRequestRepository requestRepository = new OwnerRegistrationRequestRepository();
                requestRepository.update(request).thenAccept(requestUpdated -> {
                    if(requestUpdated){
                        Toast.makeText(view.getContext(), "Request rejected successfully", Toast.LENGTH_SHORT).show();
                        requestFragment.loadRequests();

                        String recipientEmail = request.getOwner().getEmail();
                        String subject = "Rejection Notification";
                        String body = "Dear user,\n\nYour request has been rejected for the following reason:\n" +
                                rejectionReason;

                        new Thread(() -> {
                            try {
                                EmailUtil.sendEmail(recipientEmail, subject, body);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();

                        dismiss();
                    }else{
                        Toast.makeText(view.getContext(), "Failed to reject request", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }
}