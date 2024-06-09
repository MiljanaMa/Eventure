package com.example.eventure.fragments.owner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.eventure.R;
import com.example.eventure.activities.HomeActivity;
import com.example.eventure.adapters.EmployeeAdapter;
import com.example.eventure.fragments.common.EventsSchedule;
import com.example.eventure.fragments.common.LoginFragment;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.UserRole;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.settings.FragmentTransition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmployeeManagement#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmployeeManagement extends Fragment {
    private RecyclerView recyclerView;
    private List<User> employeeList;
    private EmployeeAdapter employeeAdapter;
    private UserRepository userRepository;
    private String companyId;
    private FirebaseUser currentUser;
    private static View mainView;

    public EmployeeManagement() {
    }

    public static EmployeeManagement newInstance() {
        EmployeeManagement fragment = new EmployeeManagement();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Log.i("EventApp","current user: "+currentUser.getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employee_managment, container, false);
        userRepository = new UserRepository();
        employeeList = new ArrayList<>();
        userRepository.getByUID(currentUser.getUid()).thenAccept(user -> {
                if (user != null) {
                companyId = user.getCompanyId();
                Log.d("comp", companyId);
                //dobavi sve usere
                    userRepository.getCompanyEmployees(companyId).thenAccept(users -> {
                        employeeList = users;
                        Log.d("duzina", Integer.toString(employeeList.size()));
                        initializeRecyclerView(view);
                    });
            } else {
                Log.d("Owner not found", "No such document");
            }
        });

        Button showSearchButton = view.findViewById(R.id.showSearchButton);
        showSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View searchFields  = view.findViewById(R.id.searchFields);
                if(searchFields.getVisibility() == View.VISIBLE)
                    searchFields.setVisibility(View.GONE);
                else
                    searchFields.setVisibility(View.VISIBLE);
            }
        });

        Button search = view.findViewById(R.id.searchEmployeesButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(view);
            }
        });
        Button addBtn = view.findViewById(R.id.addEmployeeBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterEmployee registerEmployee = RegisterEmployee.newInstance(EmployeeManagement.this);
                FragmentTransition.to(registerEmployee, getActivity(),
                        true, R.id.EmployeeManagement);
            }
        });
        mainView = view;
        return view;
    }
    private void initializeRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.employeeRecyclerView);
        employeeAdapter = new EmployeeAdapter(employeeList, requireActivity().getSupportFragmentManager());
        recyclerView.setAdapter(employeeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
    private void search(View view){
        EditText name = view.findViewById(R.id.searchName);
        EditText surname = view.findViewById(R.id.searchSurname);
        EditText email = view.findViewById(R.id.searchEmail);

        String nameInput = name.getText().toString().trim();
        String surnameInput = surname.getText().toString().trim();
        String emailInput = email.getText().toString().trim();

        List<User> ret = new ArrayList<>();
        for(User e: employeeList) {
            if(e.getEmail().toLowerCase().contains(emailInput) && e.getFirstName().toLowerCase().contains(nameInput) && e.getLastName().toLowerCase().contains(surnameInput)){
                ret.add(e);
            }
        }
        recyclerView = view.findViewById(R.id.employeeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new EmployeeAdapter(ret, requireActivity().getSupportFragmentManager()));
    }
    public void refreshEmployees(){
        userRepository.getCompanyEmployees(companyId).thenAccept(users -> {
            employeeList = users;
            initializeRecyclerView(mainView);
        });
    }
}