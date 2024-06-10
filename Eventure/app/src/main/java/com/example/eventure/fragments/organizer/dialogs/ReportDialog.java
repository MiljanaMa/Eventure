package com.example.eventure.fragments.organizer.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.eventure.R;
import com.example.eventure.databinding.FragmentReportDialogBinding;
import com.example.eventure.fragments.common.Ratings;
import com.example.eventure.model.Notification;
import com.example.eventure.model.Report;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.NotificationStatus;
import com.example.eventure.model.enums.ReportStatus;
import com.example.eventure.model.enums.ReportType;
import com.example.eventure.repositories.NotificationRepository;
import com.example.eventure.repositories.RatingRepository;
import com.example.eventure.repositories.ReportRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.utils.UUIDUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class ReportDialog extends DialogFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String reportedId;
    private String type;
    private FragmentReportDialogBinding binding;
    private Report report = new Report();
    private FirebaseUser currentUser;
    private static Ratings ratingsView;

    public ReportDialog() {
        // Required empty public constructor
    }

    public static ReportDialog newInstance(String companyId, String type, Ratings ratingsView1) {
        ReportDialog fragment = new ReportDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, companyId);
        args.putString(ARG_PARAM2, type);
        fragment.setArguments(args);
        ratingsView = ratingsView1;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reportedId = getArguments().getString(ARG_PARAM1);
            type = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportDialogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button btnCancel = root.findViewById(R.id.cancel_button_report);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button addBtn = root.findViewById(R.id.report_btn);
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if (binding.reportComment.getText().toString().isEmpty()) {
                    Toast.makeText(root.getContext(), "Please write your reason", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(type.equals("company")){
                    report.setType(ReportType.COMPANY);
                }else if(type.equals("rating")){
                    report.setType(ReportType.RATING);
                }else{
                    report.setType(ReportType.ORGANIZER);
                }
                report.setStatus(ReportStatus.REPORTED);
                report.setReportedId(reportedId);
                report.setCreatedOn(new Date());
                report.setReason(binding.reportComment.getText().toString());
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
                report.setReportedBy(currentUser.getUid());


                ReportRepository reportRepository = new ReportRepository();
                RatingRepository ratingRepository = new RatingRepository();
                UserRepository userRepository = new UserRepository();
                NotificationRepository notificationRepository = new NotificationRepository();

                reportRepository.create(report).thenAccept(reportCreated -> {
                    if(reportCreated){
                        Toast.makeText(root.getContext(), "Report created successfully", Toast.LENGTH_SHORT).show();
                        if(report.getType().equals(ReportType.RATING)){
                            ratingRepository.getByUID(reportedId).thenAccept(rating -> {
                               if(rating != null){
                                   rating.setReported(true);
                                   ratingRepository.update(rating).thenAccept(updated -> {
                                       if(updated){
                                           userRepository.getAdmins().thenAccept(admins -> {
                                               for (User admin : admins) {
                                                   Notification notification = new Notification(UUIDUtil.generateUUID(), "New report", "New rating report is waiting for response, reason: " + report.getReason(),
                                                           admin.getId(), currentUser.getUid(), NotificationStatus.UNREAD);
                                                   notificationRepository.create(notification).thenAccept(creationResult -> {
                                                   });
                                               }
                                               ratingsView.refreshRatings();
                                               dismiss();

                                           });
                                       }
                                   });
                               }
                            });
                        }else if(report.getType().equals(ReportType.COMPANY)){
                            userRepository.getAdmins().thenAccept(admins -> {
                                for (User admin : admins) {
                                    Notification notification = new Notification(UUIDUtil.generateUUID(), "New report", "New company report is waiting for response, reason: " + report.getReason(),
                                            admin.getId(), currentUser.getUid(), NotificationStatus.UNREAD);
                                    notificationRepository.create(notification).thenAccept(creationResult -> {
                                    });
                                }
                            });
                            dismiss();
                        }else{
                            userRepository.getAdmins().thenAccept(admins -> {
                                for (User admin : admins) {
                                    Notification notification = new Notification(UUIDUtil.generateUUID(), "New report", "New organizer report is waiting for response, reason: " + report.getReason(),
                                            admin.getId(), currentUser.getUid(), NotificationStatus.UNREAD);
                                    notificationRepository.create(notification).thenAccept(creationResult -> {
                                    });
                                }
                            });
                        }
                        dismiss();
                    }else{
                        Toast.makeText(root.getContext(), "Failed to create report", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return root;
    }
}