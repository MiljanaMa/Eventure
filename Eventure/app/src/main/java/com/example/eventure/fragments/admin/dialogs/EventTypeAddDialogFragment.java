package com.example.eventure.fragments.admin.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.adapters.SubcategoriesChecklboxAdapter;
import com.example.eventure.fragments.admin.EventTypeManagmentFragment;
import com.example.eventure.model.EventType;
import com.example.eventure.model.Subcategory;
import com.example.eventure.model.enums.SubcategoryType;
import com.example.eventure.repositories.CategoryRepository;
import com.example.eventure.repositories.EventTypeRepository;
import com.example.eventure.repositories.SubcategoryRepository;

import java.util.ArrayList;
import java.util.List;


public class EventTypeAddDialogFragment extends DialogFragment {

    private static final List<Subcategory> subcategories = new ArrayList<>();
    private static EventTypeManagmentFragment eventTypeFragment;
    private View view;
    private SubcategoryRepository subcategoryRepository;
    private SubcategoriesChecklboxAdapter subcategoriesCheckboxAdapter;
    public EventTypeAddDialogFragment() {

    }

    public static EventTypeAddDialogFragment newInstance(EventTypeManagmentFragment eventTypeManagmentFragment) {
        EventTypeAddDialogFragment fragment = new EventTypeAddDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        eventTypeFragment = eventTypeManagmentFragment;
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

        view =  inflater.inflate(R.layout.event_type_add_dialog_fragment, container, false);
        subcategoryRepository = new SubcategoryRepository();
        loadSubcategories();

        Button btnCancel = view.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button btnAdd = view.findViewById(R.id.event_type_add_button);
        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String name = ((EditText)view.findViewById(R.id.event_type_name_add)).getText().toString();
                String description = ((EditText)view.findViewById(R.id.event_type_description_add)).getText().toString();
                List<Subcategory> selectedSubcategories = subcategoriesCheckboxAdapter.getSelectedSubcategories();
                List<String> selectedSubcategoriesIds = new ArrayList<>();
                for (Subcategory subcategory : selectedSubcategories) {
                    selectedSubcategoriesIds.add(subcategory.getId());
                }

                if (name.isEmpty() || description.isEmpty()) {
                    Toast.makeText(view.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                EventType newEventType = new EventType("", name, description, selectedSubcategoriesIds, true);

                EventTypeRepository eventTypeRepository = new EventTypeRepository();
                eventTypeRepository.create(newEventType).thenAccept(eventTypeAdded -> {
                    if(eventTypeAdded){
                        Toast.makeText(view.getContext(), "Event type added successfully.", Toast.LENGTH_SHORT).show();
                        eventTypeFragment.loadEventTypes();
                        dismiss();
                    }else{
                        Toast.makeText(view.getContext(), "Failed to add new event type.", Toast.LENGTH_SHORT).show();
                    }
                });

                dismiss();
            }
        });

        return view;
    }

    private void setUpSubcategories() {
        RecyclerView subcategoriesCheckboxView = view.findViewById(R.id.subcategories_checkbox_view);
        subcategoriesCheckboxView.setLayoutManager(new LinearLayoutManager(requireContext()));
        subcategoriesCheckboxAdapter = new SubcategoriesChecklboxAdapter(subcategories);
        subcategoriesCheckboxView.setAdapter(subcategoriesCheckboxAdapter);
    }

    private void loadSubcategories(){

        final List<Subcategory> finalSubcategories = new ArrayList<>();
        subcategoryRepository.getAll().thenAccept(subcategoriesFromDB -> {
            if (subcategoriesFromDB != null) {
                setUpSubcategories();
                finalSubcategories.addAll(subcategoriesFromDB);
                subcategories.clear();
                subcategories.addAll(finalSubcategories);
            } else {
                Log.e("Failed to retrieve subcategories", "Failed to retrieve subcategories from the database");
            }
        });
    }

}