package com.example.eventure.adapters;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.fragments.admin.CategoryManagmentFragment;
import com.example.eventure.fragments.admin.Reports;
import com.example.eventure.fragments.admin.dialogs.CategoryAddDialogFragment;
import com.example.eventure.fragments.admin.dialogs.DenyReportDialog;
import com.example.eventure.fragments.common.EmployeeProfile;
import com.example.eventure.fragments.common.OwnerProfile;
import com.example.eventure.model.CurrentUser;
import com.example.eventure.model.Rating;
import com.example.eventure.model.Report;
import com.example.eventure.model.enums.ReportStatus;
import com.example.eventure.model.enums.ReportType;
import com.example.eventure.model.enums.UserRole;
import com.example.eventure.repositories.RatingRepository;
import com.example.eventure.repositories.ReportRepository;
import com.example.eventure.repositories.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private List<Report> reports;
    private FragmentManager fragmentManager;
    private Reports reportsView;

    public ReportAdapter(List<Report> reports, FragmentManager fragmentManager, Reports reportsView1) {
        this.reports = reports;
        this.fragmentManager = fragmentManager;
        reportsView = reportsView1;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //fix here
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_card, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reports.get(position);
        holder.bind(report, fragmentManager, reportsView);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        private CurrentUser currentUser;
        private LinearLayout reportCard;
        private UserRepository userRepository;
        private ReportRepository reportRepository;
        private TextView textViewName;
        private TextView textViewComment;
        private TextView textViewRating;
        private TextView textViewDate;
        private TextView textViewType;
        private Button acceptBtn;
        private Button denyBtn;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            userRepository = new UserRepository();
            reportRepository = new ReportRepository();
            userRepository.getCurrentUser().thenAccept(currentUser1 -> {
                if (currentUser1 != null)
                    currentUser = currentUser1;
            });
            reportCard = (LinearLayout) itemView.findViewById(R.id.oneReport);
            textViewName = (TextView) itemView.findViewById(R.id.textViewNameReport);
            textViewRating = (TextView) itemView.findViewById(R.id.textViewStatusReport);
            textViewComment = (TextView) itemView.findViewById(R.id.textViewCommentReport);
            textViewDate = (TextView) itemView.findViewById(R.id.textViewDateReport);
            textViewType = (TextView) itemView.findViewById(R.id.textViewTypeReport);
            acceptBtn = (Button) itemView.findViewById(R.id.acceptReportButton);
            denyBtn = (Button) itemView.findViewById(R.id.denyReportButton);
        }

        public void bind(Report report, FragmentManager fragmentManager, Reports reportsView) {
            userRepository.getByUID(report.getReportedBy()).thenAccept(user -> {
                if (user != null) {
                    textViewName.setText(user.getFirstName() + " " + user.getLastName());
                    textViewRating.setText(String.valueOf(report.getStatus()));
                    textViewComment.setText(report.getReason());
                    textViewType.setText(report.getType().toString());
                    Date createdOn = report.getCreatedOn();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    String formattedDate = sdf.format(createdOn);

                    textViewDate.setText(formattedDate);
                    if(report.getType().equals(ReportType.RATING)){
                        reportCard.setOnClickListener(v -> {
                            OwnerProfile ownerProfile = OwnerProfile.newInstance(user);
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(R.id.reportsFrameLayout, ownerProfile);
                            transaction.addToBackStack(String.valueOf(R.id.reportsFrameLayout));
                            transaction.commit();
                        });
                    }
                    if (report.getStatus().equals(ReportStatus.REPORTED)) {
                        acceptBtn.setOnClickListener(v -> {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(itemView.getContext());
                            dialog.setMessage("Are you sure you want to accept this report?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    report.setStatus(ReportStatus.ACCEPTED);
                                    reportRepository.update(report)
                                            .thenAccept(updatedReport -> {
                                                textViewRating.setText(String.valueOf(report.getStatus()));
                                                acceptBtn.setVisibility(View.GONE);
                                                denyBtn.setVisibility(View.GONE);
                                                //check if is rating and delete it
                                                if (report.getType().equals(ReportType.RATING)) {
                                                    RatingRepository ratingRepository = new RatingRepository();
                                                    ratingRepository.delete(report.getReportedId()).thenAccept(deleted -> {
                                                        dialog.dismiss();
                                                    });
                                                }
                                                dialog.dismiss();
                                            })
                                            .exceptionally(throwable -> {
                                                throwable.printStackTrace();
                                                return null;
                                            });
                                    dialog.dismiss();

                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = dialog.create();
                            alert.show();
                        });
                        //add for denyBtn
                        denyBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DenyReportDialog dialogFragment = DenyReportDialog.newInstance(report.getId(), reportsView);
                                dialogFragment.show(fragmentManager, "DenyReportDialog");
                            }
                        });

                    } else {
                        acceptBtn.setVisibility(View.GONE);
                        denyBtn.setVisibility(View.GONE);
                    }
                }
            });

        }
    }
}
