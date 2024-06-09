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
import com.example.eventure.adapters.EventTypesCheckboxAdapter;
import com.example.eventure.adapters.SubcategoriesChecklboxAdapter;
import com.example.eventure.databinding.CategoryEditDialogFragmentBinding;
import com.example.eventure.databinding.EventTypeEditDialogFragmentBinding;
import com.example.eventure.fragments.admin.EventTypeManagmentFragment;
import com.example.eventure.model.EventType;
import com.example.eventure.model.Subcategory;
import com.example.eventure.model.enums.SubcategoryType;
import com.example.eventure.repositories.EventTypeRepository;
import com.example.eventure.repositories.SubcategoryRepository;

import java.util.ArrayList;
import java.util.List;

public class EventTypeEditDialogFragment extends DialogFragment {

    private EventTypeEditDialogFragmentBinding binding;
    private static final List<Subcategory> subcategories = new ArrayList<>();
    private SubcategoriesChecklboxAdapter subcategoriesCheckboxAdapter;
    private static EventType eventType;
    private static EventTypeManagmentFragment eventTypeFragment;
    private SubcategoryRepository subcategoryRepository;
    private View view;

    public EventTypeEditDialogFragment() {
        // Required empty public constructor
    }

    public static EventTypeEditDialogFragment newInstance(EventType eventType, EventTypeManagmentFragment eventTypeManagmentFragment) {
        EventTypeEditDialogFragment fragment = new EventTypeEditDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("EVENT_TYPE", eventType);
        fragment.setArguments(args);
        eventTypeFragment = eventTypeManagmentFragment;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventType = getArguments().getParcelable("EVENT_TYPE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EventTypeEditDialogFragmentBinding.inflate(inflater, container, false);
        view =  binding.getRoot();
        subcategoryRepository = new SubcategoryRepository();

        Button btnCancel = view.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button btnEdit = view.findViewById(R.id.event_type_edit_button);
        btnEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String description = binding.eventTypeDescriptionEdit.getText().toString();
                List<Subcategory> selectedSubcategories = subcategoriesCheckboxAdapter.getSelectedSubcategories();
                List<String> selectedSubcategoriesIds = new ArrayList<>();
                for (Subcategory subcategory : selectedSubcategories) {
                    selectedSubcategoriesIds.add(subcategory.getId());
                }

                if (description.isEmpty()) {
                    Toast.makeText(view.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                eventType.setDescription(description);
                eventType.setSuggestedSubcategoriesIds(selectedSubcategoriesIds);

                EventTypeRepository eventTypeRepository = new EventTypeRepository();
                eventTypeRepository.update(eventType).thenAccept(eventTypeAdded -> {
                    if(eventTypeAdded){
                        Toast.makeText(view.getContext(), "Event type updated successfully.", Toast.LENGTH_SHORT).show();
                        eventTypeFragment.loadEventTypes();
                        dismiss();
                    }else{
                        Toast.makeText(view.getContext(), "Failed to update event type.", Toast.LENGTH_SHORT).show();
                    }
                });

                dismiss();
            }
        });

        loadSubcategories();

        Bundle args = getArguments();
        if (args != null) {
            binding.eventTypeNameNoEdit.setText(eventType.getName());
            binding.eventTypeDescriptionEdit.setText(eventType.getDescription());
        }

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
                subcategoriesCheckboxAdapter.setCheckedSubcategories(eventType.getSuggestedSubcategoriesIds());
            } else {
                Log.e("Failed to retrieve subcategories", "Failed to retrieve subcategories from the database");
            }
        });
    }
}