package com.example.eventure.fragments.admin.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.databinding.CategoryAddDialogFragmentBinding;
import com.example.eventure.fragments.admin.CategoryManagmentFragment;
import com.example.eventure.model.Category;
import com.example.eventure.repositories.CategoryRepository;

public class CategoryAddDialogFragment extends DialogFragment {

    private CategoryAddDialogFragmentBinding binding;
    private static CategoryManagmentFragment categoryFragment;

    public CategoryAddDialogFragment() {;
    }

    public static CategoryAddDialogFragment newInstance(CategoryManagmentFragment categoryManagmentFragment) {
        CategoryAddDialogFragment fragment = new CategoryAddDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        categoryFragment = categoryManagmentFragment;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = CategoryAddDialogFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button btnCancel = root.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               dismiss();
            }
        });

        Button btnAdd = root.findViewById(R.id.category_add_button);
        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String name = ((EditText)root.findViewById(R.id.category_name_add)).getText().toString();
                String description = ((EditText)root.findViewById(R.id.category_description_add)).getText().toString();

                if (name.isEmpty() || description.isEmpty()) {
                    Toast.makeText(root.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                Category newCategory = new Category("", name, description);

                CategoryRepository categoryRepository = new CategoryRepository();
                categoryRepository.create(newCategory).thenAccept(categoryAdded -> {
                    if(categoryAdded){
                        Toast.makeText(root.getContext(), "Category added successfully.", Toast.LENGTH_SHORT).show();
                        categoryFragment.loadCategories();
                        dismiss();
                    }else{
                        Toast.makeText(root.getContext(), "Failed to add new category.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return root;
    }

}