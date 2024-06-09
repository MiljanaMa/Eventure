package com.example.eventure.fragments.admin.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.databinding.SubcategoryAddDialogFragmentBinding;
import com.example.eventure.fragments.admin.SubcategoryManagmentFragment;
import com.example.eventure.model.Category;
import com.example.eventure.model.Subcategory;
import com.example.eventure.model.enums.SubcategoryType;
import com.example.eventure.repositories.CategoryRepository;
import com.example.eventure.repositories.SubcategoryRepository;

public class SubcategoryAddDialogFragment extends DialogFragment {

    private SubcategoryAddDialogFragmentBinding binding;
    private static SubcategoryManagmentFragment subcategoryFragment;
    private String categoryId;


    public SubcategoryAddDialogFragment() {;
    }

    public static SubcategoryAddDialogFragment newInstance(String categoryId, SubcategoryManagmentFragment subcategoryManagmentFragment) {
        SubcategoryAddDialogFragment fragment = new SubcategoryAddDialogFragment();
        Bundle args = new Bundle();
        args.putString("CategoryId", categoryId);
        fragment.setArguments(args);
        subcategoryFragment = subcategoryManagmentFragment;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            categoryId = getArguments().getString("CategoryId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = SubcategoryAddDialogFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button btnCancel = root.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button btnAdd = root.findViewById(R.id.subcategory_add_button);
        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String name = ((EditText)root.findViewById(R.id.subcategory_name_add)).getText().toString();
                String description = ((EditText)root.findViewById(R.id.subcategory_description_add)).getText().toString();
                String typeString = ((Spinner)root.findViewById(R.id.subcategory_type_spinner)).getSelectedItem().toString();

                if (name.isEmpty() || description.isEmpty()) {
                    Toast.makeText(root.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                SubcategoryType type = SubcategoryType.valueOf(typeString);

                Subcategory newSubcategory = new Subcategory("", name, description, categoryId, type);

                SubcategoryRepository subcategoryRepository = new SubcategoryRepository();
                subcategoryRepository.create(newSubcategory).thenAccept(subcategoryId -> {
                    if(subcategoryId != null){
                        Toast.makeText(root.getContext(), "Subcategory added successfully.", Toast.LENGTH_SHORT).show();
                        subcategoryFragment.loadSubcategories();
                        dismiss();
                    }else{
                        Toast.makeText(root.getContext(), "Failed to add new subcategory.", Toast.LENGTH_SHORT).show();
                    }
                });

                dismiss();
            }
        });

        return root;
    }
}