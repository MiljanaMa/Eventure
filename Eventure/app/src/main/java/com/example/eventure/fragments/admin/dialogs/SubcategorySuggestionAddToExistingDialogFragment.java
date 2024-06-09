package com.example.eventure.fragments.admin.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.databinding.SubcategoryEditDialogFragmentBinding;
import com.example.eventure.databinding.SubcategorySuggestionAddToExistingDialogFragmentBinding;
import com.example.eventure.fragments.admin.SubcategorySuggestionManagmentFragment;
import com.example.eventure.model.Subcategory;
import com.example.eventure.model.SubcategorySuggestion;
import com.example.eventure.repositories.SubcategoryRepository;
import com.example.eventure.repositories.SubcategorySuggestionRepository;

import java.util.ArrayList;
import java.util.List;


public class SubcategorySuggestionAddToExistingDialogFragment extends DialogFragment {

    private SubcategorySuggestionAddToExistingDialogFragmentBinding binding;
    private SubcategorySuggestion suggestion;
    private static SubcategorySuggestionManagmentFragment suggestionFragment;
    private List<Subcategory> subcategories = new ArrayList<>();
    private Spinner subcategorySpinner;

    public SubcategorySuggestionAddToExistingDialogFragment() {
        // Required empty public constructor
    }


    public static SubcategorySuggestionAddToExistingDialogFragment newInstance(SubcategorySuggestion subcategorySuggestion, SubcategorySuggestionManagmentFragment subcategorySuggestionFragment) {
        SubcategorySuggestionAddToExistingDialogFragment fragment = new SubcategorySuggestionAddToExistingDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("Suggestion", subcategorySuggestion);
        fragment.setArguments(args);
        suggestionFragment = subcategorySuggestionFragment;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            suggestion = getArguments().getParcelable("Suggestion");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = SubcategorySuggestionAddToExistingDialogFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        SubcategoryRepository subcategoryRepository = new SubcategoryRepository();
        subcategoryRepository.getAll().thenAccept(subcategoriesFromDB -> {
            if(subcategoriesFromDB != null){
                subcategories.addAll(subcategoriesFromDB);
                ArrayAdapter<Subcategory> subcategoryAdapter = new ArrayAdapter<Subcategory>(view.getContext(),
                        android.R.layout.simple_spinner_item, subcategories);
                subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subcategorySpinner = view.findViewById(R.id.subcategory_spinner);
                subcategorySpinner.setAdapter(subcategoryAdapter);
            }
        });

        Button btnCancel = view.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button btnAdd = view.findViewById(R.id.add_button);
        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Subcategory subcategory = (Subcategory) subcategorySpinner.getSelectedItem();
                suggestion.setSubcategory(subcategory);
                suggestion.getProduct().setCategoryId(subcategory.getCategoryId());
                SubcategorySuggestionRepository suggestionRepository = new SubcategorySuggestionRepository();
                suggestionRepository.update(suggestion).thenAccept(suggestionUpdated -> {
                    if(suggestionUpdated){
                        Toast.makeText(view.getContext(), "Suggestion updated successfully.", Toast.LENGTH_SHORT).show();
                        suggestionFragment.loadSuggestions();
                        dismiss();
                    }else{
                        Toast.makeText(view.getContext(), "Failed to update new suggestion.", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        return view;
    }
}