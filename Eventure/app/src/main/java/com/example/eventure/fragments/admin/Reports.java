package com.example.eventure.fragments.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.ReportAdapter;
import com.example.eventure.model.Category;
import com.example.eventure.model.Report;
import com.example.eventure.repositories.ReportRepository;
import com.example.eventure.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class Reports extends Fragment {
    private RecyclerView recyclerView;
    private List<Report> reports;
    private ReportAdapter reportAdapter;
    private ReportRepository reportRepository;
    private static View mainView;

    public Reports() {
        // Required empty public constructor
    }

    public static Reports newInstance() {
        Reports fragment = new Reports();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        reportRepository = new ReportRepository();
        reports = new ArrayList<>();
        reportRepository.getAll().thenAccept(reports1 -> {
            reports = reports1;
            initializeRecyclerView(view);
        });

        mainView = view;
        return view;
    }

    private void initializeRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.reportsRecyclerView);
        reportAdapter = new ReportAdapter(reports, requireActivity().getSupportFragmentManager(), Reports.this);
        recyclerView.setAdapter(reportAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
    public void refreshReports(){
        reportRepository.getAll().thenAccept(reportsFromDb -> {
            if (reportsFromDb != null) {
                reports = reportsFromDb;
                initializeRecyclerView(mainView);
            } else {
                Log.e("Failed to retrieve categories", "Failed to retrieve categories from the database");
            }
        });
    }

}