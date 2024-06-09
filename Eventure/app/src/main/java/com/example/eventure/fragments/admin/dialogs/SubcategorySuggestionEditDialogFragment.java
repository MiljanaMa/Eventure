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
import com.example.eventure.databinding.SubcategorySuggestionEditDialogFragmentBinding;
import com.example.eventure.fragments.admin.SubcategorySuggestionManagmentFragment;
import com.example.eventure.model.Category;
import com.example.eventure.model.Subcategory;
import com.example.eventure.model.SubcategorySuggestion;
import com.example.eventure.model.enums.SubcategoryType;
import com.example.eventure.repositories.CategoryRepository;
import com.example.eventure.repositories.SubcategorySuggestionRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SubcategorySuggestionEditDialogFragment extends DialogFragment {

    private SubcategorySuggestionEditDialogFragmentBinding binding;
    private static SubcategorySuggestionManagmentFragment suggestionFragment;
    private SubcategorySuggestion suggestion;
    private List<Category> categories = new ArrayList<>();
    private Spinner categorySpinner;
    private CategoryRepository categoryRepository;

    public SubcategorySuggestionEditDialogFragment() {
        // Required empty public constructor
    }


    public static SubcategorySuggestionEditDialogFragment newInstance(SubcategorySuggestion selectedSuggestion, SubcategorySuggestionManagmentFragment suggestionManagmentFragment) {
        SubcategorySuggestionEditDialogFragment fragment = new SubcategorySuggestionEditDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("Suggestion", selectedSuggestion);
        fragment.setArguments(args);
        suggestionFragment = suggestionManagmentFragment;
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
        binding = SubcategorySuggestionEditDialogFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        categoryRepository = new CategoryRepository();

        categoryRepository.getAll().thenAccept(categoriesFromDB -> {
            if(categoriesFromDB != null){
                categories.addAll(categoriesFromDB);
                ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<Category>(view.getContext(),
                        android.R.layout.simple_spinner_item, categories);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner = view.findViewById(R.id.category_spinner);
                categorySpinner.setAdapter(categoryAdapter);

                Bundle args = getArguments();
                if (args != null) {
                    binding.subcategorySuggestionNameEdit.setText(suggestion.getSubcategory().getName());
                    binding.subcategoryDescriptionEdit.setText(suggestion.getSubcategory().getDescription());
                    binding.subcategoryTypeEdit.setText(suggestion.getSubcategory().getType().toString());

                    categoryRepository.getByUID(suggestion.getSubcategory().getCategoryId()).thenAccept(category -> {
                        if(category != null){
                            int categoryIndex = categories.indexOf(category);
                            categorySpinner.setSelection(categoryIndex);
                        }
                    });
                }

            }
        });

        Button btnCancel = view.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button btnEdit = view.findViewById(R.id.edit_button);
        btnEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String name = binding.subcategorySuggestionNameEdit.getText().toString();
                String description = binding.subcategoryDescriptionEdit.getText().toString();
                Category category = (Category) binding.categorySpinner.getSelectedItem();
                SubcategoryType type = SubcategoryType.valueOf(binding.subcategoryTypeEdit.getText().toString());

                if (name.isEmpty() || description.isEmpty()) {
                    Toast.makeText(view.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                Subcategory subcategory = new Subcategory("", name, description, category.getId(), type);
                suggestion.setSubcategory(subcategory);
                suggestion.getProduct().setCategoryId(category.getId());

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



        return view ;
    }
}