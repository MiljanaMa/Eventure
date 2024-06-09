package com.example.eventure.fragments.admin.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.eventure.R;
import com.example.eventure.databinding.FragmentDenyReportDialogBinding;
import com.example.eventure.databinding.FragmentRatingDialogBinding;
import com.example.eventure.fragments.admin.Reports;
import com.example.eventure.model.Notification;
import com.example.eventure.model.Rating;
import com.example.eventure.model.Report;
import com.example.eventure.model.enums.NotificationStatus;
import com.example.eventure.model.enums.ReportStatus;
import com.example.eventure.model.enums.ReportType;
import com.example.eventure.repositories.CompanyRepository;
import com.example.eventure.repositories.NotificationRepository;
import com.example.eventure.repositories.RatingRepository;
import com.example.eventure.repositories.ReportRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.utils.UUIDUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class DenyReportDialog extends DialogFragment {
    private static final String ARG_PARAM1 = "param1";
    private String reportId;
    private FragmentDenyReportDialogBinding binding;
    private Report report;
    private static Reports reportsView;
    private FirebaseUser currentUser;

    public DenyReportDialog() {
        // Required empty public constructor
    }

    public static DenyReportDialog newInstance(String reportId, Reports reports) {
        DenyReportDialog fragment = new DenyReportDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, reportId);
        fragment.setArguments(args);
        reportsView = reports;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reportId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDenyReportDialogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button btnCancel = root.findViewById(R.id.cancel_button_deny_report);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button addBtn = root.findViewById(R.id.report_deny_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.reportDenyComment.getText().toString().isEmpty()) {
                    Toast.makeText(root.getContext(), "Please write reason of denying", Toast.LENGTH_SHORT).show();
                    return;
                }
                ReportRepository reportRepository = new ReportRepository();
                NotificationRepository notificationRepository = new NotificationRepository();
                RatingRepository ratingRepository = new RatingRepository();
                UserRepository userRepository = new UserRepository();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();


                reportRepository.getByUID(reportId).thenAccept(report1 -> {
                    report = report1;
                    report.setRejectionReason(binding.reportDenyComment.getText().toString());
                    report.setStatus(ReportStatus.REJECTED);

                    reportRepository.update(report).thenAccept(reportUpdated -> {
                        if (reportUpdated) {
                            Toast.makeText(root.getContext(), "Report denied successfully", Toast.LENGTH_SHORT).show();

                            if (report.getType().equals(ReportType.RATING)) {
                                ratingRepository.getByUID(report.getReportedId()).thenAccept(rating -> {
                                    if (rating != null) {
                                        userRepository.getCompanyOwner(rating.getCompanyId()).thenAccept(owner -> {
                                            if(owner != null){
                                                Notification notification = new Notification(UUIDUtil.generateUUID(), "Report denied", "Your report is denied because " + report.getRejectionReason(),
                                                        owner.getId(), currentUser.getUid(), NotificationStatus.UNREAD);
                                                notificationRepository.create(notification).thenAccept(creationResult -> {
                                                    reportsView.refreshReports();
                                                    dismiss();
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(root.getContext(), "Failed to deny report", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });
        return root;
    }
}