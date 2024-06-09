package com.example.eventure.fragments.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.eventure.R;
import com.example.eventure.adapters.SubcategoryListAdapter;
import com.example.eventure.fragments.admin.dialogs.SubcategoryAddDialogFragment;
import com.example.eventure.model.Subcategory;
import com.example.eventure.repositories.SubcategoryRepository;

import java.util.ArrayList;
import java.util.List;

public class   SubcategoryManagmentFragment extends Fragment {

    private static final List<Subcategory> subcategories = new ArrayList<>();
    private String categoryId;
    private View view;
    private SubcategoryRepository subcategoryRepository;

    public SubcategoryManagmentFragment() {

    }

    public static SubcategoryManagmentFragment newInstance(String companyId) {
        SubcategoryManagmentFragment fragment = new SubcategoryManagmentFragment();
        Bundle args = new Bundle();
        args.putString("CategoryId", companyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString("CategoryId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.subcategory_managment_fragment, container, false);
        subcategoryRepository = new SubcategoryRepository();

        ImageView btnAddSubcategory = view.findViewById(R.id.add_subcategory_button);

        btnAddSubcategory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SubcategoryAddDialogFragment dialogFragment = SubcategoryAddDialogFragment.newInstance(categoryId,SubcategoryManagmentFragment.this);
                dialogFragment.show(requireActivity().getSupportFragmentManager(), "SubcategoryAddDialogFragment");
            }
        });

        ImageView btnBack = view.findViewById(R.id.back_button);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(String.valueOf(R.id.categories_fragment_container), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        loadSubcategories();

        return view;
    }

    private void setUpSubcategories() {
        RecyclerView subcategoryListView = view.findViewById(R.id.subcategories_view);
        subcategoryListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        SubcategoryListAdapter subcategoryListAdapter = new SubcategoryListAdapter(subcategories, requireActivity().getSupportFragmentManager(), SubcategoryManagmentFragment.this);
        subcategoryListView.setAdapter(subcategoryListAdapter);
    }

    public void loadSubcategories(){

        final List<Subcategory> finalSubcategories = new ArrayList<>();
        subcategoryRepository.getByCategoryId(categoryId).thenAccept(subcategoriesFromDB -> {
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