package com.example.eventure.fragments.owner;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.eventure.R;
import com.example.eventure.adapters.EmployeeCheckboxAdapter;
import com.example.eventure.adapters.EventTypesCheckboxAdapter;
import com.example.eventure.model.Employee;
import com.example.eventure.model.EventType;

import java.util.ArrayList;
import java.util.List;


public class ServiceEditFormFragment extends Fragment {

    private static final int GET_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private List<Employee> employees;
    private RecyclerView employeesCheckboxView;
    private EmployeeCheckboxAdapter employeesCheckboxAdapter;

    private List<EventType> eventTypes;
    private RecyclerView eventTypesCheckboxView;
    private EventTypesCheckboxAdapter eventTypesCheckboxAdapter;

    public ServiceEditFormFragment() {
        // Required empty public constructor
    }

    public static ServiceEditFormFragment newInstance() {
        ServiceEditFormFragment fragment = new ServiceEditFormFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.service_edit_form_fragment, container, false);

        setButtons(view);
        setUpEmployees(view);
        setUpEventTypes(view);

        return view;
    }

    private void setButtons(View view) {
        Button btnSuggestNewSubcategory = view.findViewById(R.id.suggest_subcategory_button);
        btnSuggestNewSubcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                view.findViewById(R.id.subcategory_container).setVisibility(View.GONE);
                view.findViewById(R.id.subcategory_suggestion_containter).setVisibility((View.VISIBLE));
            }
        });

        Button btnAddExistingSubcategory = view.findViewById(R.id.add_existing_subcategory_button);
        btnAddExistingSubcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                view.findViewById(R.id.subcategory_container).setVisibility(View.VISIBLE);
                view.findViewById(R.id.subcategory_suggestion_containter).setVisibility((View.GONE));
            }
        });

        Button btnUploadImage  = view.findViewById(R.id.upload_image_button);
        imageView = view.findViewById(R.id.imageView);

        btnUploadImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GET_IMAGE_REQUEST);
            }
        });

        Button btnCancel = view.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(String.valueOf(R.id.owner_service_details_container), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        Button btnAddService = view.findViewById(R.id.service_edit_button);
        btnAddService.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(String.valueOf(R.id.owner_service_details_container), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
    }

    private void setUpEmployees(View view) {
        employeesCheckboxView = view.findViewById(R.id.employees_checkbox_view);
        employeesCheckboxView.setLayoutManager(new LinearLayoutManager(requireContext()));

        employees = new ArrayList<>();
        prepareEmployees(employees);

        employeesCheckboxAdapter = new EmployeeCheckboxAdapter(employees);
        employeesCheckboxView.setAdapter(employeesCheckboxAdapter);
    }

    private void prepareEmployees(List<Employee> employees){

        //employees.add(new Employee("Miljana", "Marjanovic", "milja@gmail.com", "06485656", "Junaka 2", null, true));
        //employees.add(new Employee("Milena", "Markovic", "mila@gmail.com", "061588954", "Junaka 3", null, true));
        //employees.add(new Employee("Strahinja", "Praska", "praska@gmail.com", "061588954", "Junaka 4", null, false));
        //employees.add(new Employee("Ana", "Nov", "anastano@gmail.com", "061588954", "Junaka 2", null, false));
    }

    private void setUpEventTypes(View view) {
        eventTypesCheckboxView = view.findViewById(R.id.events_types_checkbox_view);
        eventTypesCheckboxView.setLayoutManager(new LinearLayoutManager(requireContext()));

        eventTypes = new ArrayList<>();
        prepareEventTypes(eventTypes);

        eventTypesCheckboxAdapter = new EventTypesCheckboxAdapter(eventTypes);
        eventTypesCheckboxView.setAdapter(eventTypesCheckboxAdapter);
    }

    private void prepareEventTypes(List<EventType> eventTypes){

        eventTypes.add(new EventType("1L", "Event Type 1", "Description 1", null, false));
        eventTypes.add(new EventType("2L", "Event Type 2", "Description 2", null, false));
        eventTypes.add(new EventType("3L", "Event Type 3", "Description 3", null, false));
        eventTypes.add(new EventType("4L", "Event Type 4", "Description 4", null, false));
    }

}