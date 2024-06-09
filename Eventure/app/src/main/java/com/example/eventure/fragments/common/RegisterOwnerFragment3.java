package com.example.eventure.fragments.common;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.adapters.CategoryCheckboxAdapter;
import com.example.eventure.adapters.EventTypesCheckboxAdapter;
import com.example.eventure.model.Category;
import com.example.eventure.model.EventType;
import com.example.eventure.model.OwnerRegistrationRequest;
import com.example.eventure.repositories.CategoryRepository;
import com.example.eventure.repositories.EventTypeRepository;
import com.example.eventure.settings.FragmentTransition;

import java.util.ArrayList;
import java.util.List;

public class RegisterOwnerFragment3 extends Fragment {

    private static final List<Category> categories = new ArrayList<>();;
    private CategoryCheckboxAdapter categoryCheckboxAdapter;
    private static final List<EventType> eventTypes = new ArrayList<>();
    private EventTypesCheckboxAdapter eventTypesCheckboxAdapter;
    private OwnerRegistrationRequest request;

    public RegisterOwnerFragment3() {
    }


    public static RegisterOwnerFragment3 newInstance(OwnerRegistrationRequest registrationRequest) {
        RegisterOwnerFragment3 fragment = new RegisterOwnerFragment3();
        Bundle args = new Bundle();
        args.putParcelable("REQUEST", registrationRequest);
        fragment.setArguments(args);
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

        View view = inflater.inflate(R.layout.register_owner_fragment3, container, false);

        Button btnNext  = view.findViewById(R.id.next_button);
        btnNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                List<Category> selectedCategories = categoryCheckboxAdapter.getSelectedCategories();

                if (selectedCategories.isEmpty()) {
                    Toast.makeText(getContext(), "Please select at least one category", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> selectedCategoriesIds = new ArrayList<>();
                for (Category category : selectedCategories) {
                    selectedCategoriesIds.add(category.getId());
                }
                request.getCompany().setCategoriesIds(selectedCategoriesIds);

                List<EventType> selectedEvetTypes = eventTypesCheckboxAdapter.getSelectedEventTypes();
                List<String> selectedEventTypesIds = new ArrayList<>();
                for (EventType eventType : selectedEvetTypes) {
                    selectedEventTypesIds.add(eventType.getId());
                }
                request.getCompany().setEventTypesIds(selectedEventTypesIds);

                RegisterOwnerFragment4 registerOwnerFragment4 = RegisterOwnerFragment4.newInstance(request);
                FragmentTransition.to(registerOwnerFragment4, getActivity(),
                        true, R.id.register_owner_container3);
            }
        });

        Button btnBack  = view.findViewById(R.id.back_button);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(String.valueOf(R.id.register_owner_container2), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        prepareCategories(view);
        prepareEventTypes(view);

        return view;
    }


    private void setUpCategories(View view) {
        RecyclerView categoriesCheckboxView = view.findViewById(R.id.categories_checkbox_view);
        categoriesCheckboxView.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoryCheckboxAdapter = new CategoryCheckboxAdapter(categories);
        categoriesCheckboxView.setAdapter(categoryCheckboxAdapter);
    }


    private void setUpEventTypes(View view) {
        RecyclerView eventTypesCheckboxView = view.findViewById(R.id.events_checkbox_view);
        eventTypesCheckboxView.setLayoutManager(new LinearLayoutManager(requireContext()));
        eventTypesCheckboxAdapter = new EventTypesCheckboxAdapter(eventTypes);
        eventTypesCheckboxView.setAdapter(eventTypesCheckboxAdapter);
    }

    private void prepareCategories(View view){
        CategoryRepository categoryRepository = new CategoryRepository();
        final List<Category> finalCategories = new ArrayList<>();
        categoryRepository.getAll().thenAccept(categoriesFromDB -> {
            if (categoriesFromDB != null) {
                setUpCategories(view);
                finalCategories.addAll(categoriesFromDB);
                categories.clear();
                categories.addAll(finalCategories);

            } else {
                Log.e("Failed to retrieve categories", "Failed to retrieve categories from the database");
            }
        });
    }

    private void prepareEventTypes(View view){
        EventTypeRepository eventTypeRepository = new EventTypeRepository();
        final List<EventType> finalEventTypes = new ArrayList<>();
        eventTypeRepository.getAll().thenAccept(eventTypesFromDB -> {
            if (eventTypesFromDB != null) {
                setUpEventTypes(view);
                finalEventTypes.addAll(eventTypesFromDB);
                eventTypes.clear();
                eventTypes.addAll(finalEventTypes);
            } else {
                Log.e("Failed to retrieve event types", "Failed to retrieve event types from the database");
            }
        });
    }
}
