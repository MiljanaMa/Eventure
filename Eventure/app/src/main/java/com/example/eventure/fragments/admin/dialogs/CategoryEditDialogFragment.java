package com.example.eventure.fragments.admin.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.databinding.CategoryEditDialogFragmentBinding;
import com.example.eventure.fragments.admin.CategoryManagmentFragment;
import com.example.eventure.model.Category;
import com.example.eventure.repositories.CategoryRepository;

public class CategoryEditDialogFragment extends DialogFragment {

    private CategoryEditDialogFragmentBinding binding;
    private static Category category;
    private static CategoryManagmentFragment categoryFragment;

    public CategoryEditDialogFragment() {;
    }

    public static CategoryEditDialogFragment newInstance(Category selectedCategory, CategoryManagmentFragment categoryManagmentFragment) {
        CategoryEditDialogFragment fragment = new CategoryEditDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("CATEGORY", selectedCategory);
        fragment.setArguments(args);
        categoryFragment = categoryManagmentFragment;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getParcelable("CATEGORY");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = CategoryEditDialogFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button btnCancel = root.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button btnEdit = root.findViewById(R.id.category_edit_button);
        btnEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                category.setName(binding.categoryNameEdit.getText().toString());
                category.setDescription(binding.categoryDescriptionEdit.getText().toString());

                if (binding.categoryNameEdit.getText().toString().isEmpty() || binding.categoryDescriptionEdit.getText().toString().isEmpty()) {
                    Toast.makeText(root.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                CategoryRepository categoryRepository = new CategoryRepository();
                categoryRepository.update(category).thenAccept(categoryUpdated -> {
                    if(categoryUpdated){
                        Toast.makeText(root.getContext(), "Category updated successfully", Toast.LENGTH_SHORT).show();
                        categoryFragment.loadCategories();
                        dismiss();
                    }else{
                        Toast.makeText(root.getContext(), "Failed to update category", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            binding.categoryNameEdit.setText(category.getName());
            binding.categoryDescriptionEdit.setText(category.getDescription());
        }

        return root;
    }
}