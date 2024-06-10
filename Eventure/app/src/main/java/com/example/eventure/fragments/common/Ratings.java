package com.example.eventure.fragments.common;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.eventure.R;
import com.example.eventure.adapters.EmployeeAdapter;
import com.example.eventure.adapters.RatingAdapter;
import com.example.eventure.fragments.owner.EmployeeManagement;
import com.example.eventure.fragments.owner.RegisterEmployee;
import com.example.eventure.model.Rating;
import com.example.eventure.model.User;
import com.example.eventure.repositories.RatingRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.settings.FragmentTransition;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Ratings extends Fragment {
    private static final String ARG_PARAM1 = "";
    private String mParam1;
    private UserRepository userRepository;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private List<Rating> ratings;
    private RatingAdapter ratingAdapter;
    private RatingRepository ratingRepository;
    private String companyId = "";
    private static View mainView;
    private TextInputEditText weekPickerEditTextFrom;
    private TextInputEditText weekPickerEditTextTo;
    private Button openWeekPickerButtonFrom;
    private Button openWeekPickerButtonTo;
    private Date from;
    private Date to;

    public Ratings() {
        // Required empty public constructor
    }

    public static Ratings newInstance(String param1) {
        Ratings fragment = new Ratings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            companyId = mParam1;
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ratings, container, false);
        userRepository = new UserRepository();
        ratingRepository = new RatingRepository();
        ratings = new ArrayList<>();
        if (companyId.equals("")) {
            userRepository.getByUID(currentUser.getUid()).thenAccept(user -> {
                if (user != null) {
                    companyId = user.getCompanyId();
                    ratingRepository.getAllByCompanyId(companyId).thenAccept(ratings1 -> {
                        ratings = ratings1;
                        initializeRecyclerView(view);
                    });
                } else {
                    Log.d("Owner not found", "No such document");
                }
            });
        } else {
            ratingRepository.getAllByCompanyId(companyId).thenAccept(ratings1 -> {
                ratings = ratings1;
                initializeRecyclerView(view);
            });
        }
        LinearLayout filter = view.findViewById(R.id.filterView);
        if (mParam1 != null) {
            filter.setVisibility(View.GONE);
        }
        Button filterBtn = view.findViewById(R.id.filterRatingsBtn);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter(view);
            }
        });
        Button resetBtn = view.findViewById(R.id.resetFilterBtn);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshRatings();
                weekPickerEditTextFrom.setText("");
                weekPickerEditTextTo.setText("");
            }
        });
        configureDatePickers(view);
        mainView = view;
        return view;
    }

    private void initializeRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.ratingsRecyclerView);
        ratingAdapter = new RatingAdapter(ratings, requireActivity().getSupportFragmentManager(), Ratings.this);
        recyclerView.setAdapter(ratingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void filter(View view) {

        Date fromDate = new Date(from.getYear() - 1900, from.getMonth() - 1, from.getDate());
        Date toDate = new Date(to.getYear() - 1900, to.getMonth() - 1, to.getDate());
        // Get the end date from the DatePicker


        List<Rating> ret = new ArrayList<>();
        for (Rating r : ratings) {
            Date createdDate = r.getCreatedOn();
            if (createdDate != null && !createdDate.before(fromDate) && !createdDate.after(toDate)) {
                ret.add(r);
            }
        }
        recyclerView = view.findViewById(R.id.ratingsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new RatingAdapter(ret, requireActivity().getSupportFragmentManager(), Ratings.this));
    }

    public void refreshRatings() {
        ratingRepository.getAllByCompanyId(companyId).thenAccept(ratings1 -> {
            ratings = ratings1;
            initializeRecyclerView(mainView);
        });
    }
    private void configureDatePickers(View view){
        weekPickerEditTextFrom = view.findViewById(R.id.weekPickerEditTextRatingFrom);
        openWeekPickerButtonFrom = view.findViewById(R.id.openWeekPickerButtonFrom);
        openWeekPickerButtonFrom.setOnClickListener(v -> showDatePicker(weekPickerEditTextFrom, true, view));
        weekPickerEditTextTo = view.findViewById(R.id.weekPickerEditTextRatingTo);
        openWeekPickerButtonTo = view.findViewById(R.id.openWeekPickerButtonTo);
        openWeekPickerButtonTo.setOnClickListener(v -> showDatePicker(weekPickerEditTextTo,false, view));
    }
    private void showDatePicker(TextInputEditText datePicker, boolean fromDate, View view1) {
        final android.icu.util.Calendar calendar = android.icu.util.Calendar.getInstance();
        int year = calendar.get(android.icu.util.Calendar.YEAR);
        int month = calendar.get(android.icu.util.Calendar.MONTH);
        int dayOfMonth = calendar.get(android.icu.util.Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                view1.getContext(),
                (view, year1, monthOfYear, dayOfMonth1) -> {
                        datePicker.setText(String.format(Locale.getDefault(), "%02d.%02d.%04d", dayOfMonth1, monthOfYear + 1, year1));
                        if(fromDate)
                            from = new Date(year1, monthOfYear + 1, dayOfMonth1);
                        else
                            to = new Date(year1, monthOfYear + 1, dayOfMonth1);

                },
                year,
                month,
                dayOfMonth
        );
        datePickerDialog.show();
    }
}