package com.example.eventure.fragments.admin.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.databinding.SubcategoryEditDialogFragmentBinding;
import com.example.eventure.fragments.admin.SubcategoryManagmentFragment;
import com.example.eventure.model.Subcategory;
import com.example.eventure.model.enums.SubcategoryType;
import com.example.eventure.repositories.SubcategoryRepository;

import java.util.Arrays;

public class SubcategoryEditDialogFragment extends DialogFragment {

    private SubcategoryEditDialogFragmentBinding binding;
    private static SubcategoryManagmentFragment subcategoryFragment;
    private Subcategory subcategory;

    public SubcategoryEditDialogFragment() {;
    }

    public static SubcategoryEditDialogFragment newInstance(Subcategory selectedSubcategory, SubcategoryManagmentFragment subcategoryManagmentFragment) {
        SubcategoryEditDialogFragment fragment = new SubcategoryEditDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("Subcategory", selectedSubcategory);
        fragment.setArguments(args);
        subcategoryFragment = subcategoryManagmentFragment;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            subcategory = getArguments().getParcelable("Subcategory");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = SubcategoryEditDialogFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button btnCancel = root.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button btnEdit = root.findViewById(R.id.subcategory_edit_button);
        btnEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String name = binding.subcategoryNameEdit.getText().toString();
                String description = binding.subcategoryDescriptionEdit.getText().toString();
                SubcategoryType type = SubcategoryType.valueOf(binding.subcategoryTypeSpinner.getSelectedItem().toString());

                if (name.isEmpty() || description.isEmpty()) {
                    Toast.makeText(root.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                subcategory.setName(name);
                subcategory.setDescription(description);
                subcategory.setType(type);

                SubcategoryRepository subcategoryRepository = new SubcategoryRepository();
                subcategoryRepository.update(subcategory).thenAccept(subcategoryAdded -> {
                    if(subcategoryAdded){
                        Toast.makeText(root.getContext(), "Subcategory updated successfully.", Toast.LENGTH_SHORT).show();
                        subcategoryFragment.loadSubcategories();
                        dismiss();
                    }else{
                        Toast.makeText(root.getContext(), "Failed to update new subcategory.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            binding.subcategoryNameEdit.setText(subcategory.getName());
            binding.subcategoryDescriptionEdit.setText(subcategory.getDescription());
            String[] subcategoryTypes = getResources().getStringArray(R.array.subcategory_type_spinner_array);
            int index = Arrays.asList(subcategoryTypes).indexOf(subcategory.getType().toString());
            binding.subcategoryTypeSpinner.setSelection(index);
        }

        return root;
    }
}