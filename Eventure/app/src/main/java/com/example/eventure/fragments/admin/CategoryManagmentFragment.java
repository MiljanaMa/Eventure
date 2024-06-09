package com.example.eventure.fragments.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.eventure.R;
import com.example.eventure.adapters.CategoryListAdapter;
import com.example.eventure.fragments.admin.dialogs.CategoryAddDialogFragment;
import com.example.eventure.model.Category;
import com.example.eventure.repositories.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

public class CategoryManagmentFragment extends Fragment {

    private static final List<Category> categories = new ArrayList<>();
    private static CategoryRepository categoryRepository;
    private View view;

    public CategoryManagmentFragment() {

    }

    public static CategoryManagmentFragment newInstance() {
        CategoryManagmentFragment fragment = new CategoryManagmentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        view = inflater.inflate(R.layout.category_managment_fragment, container, false);
        categoryRepository = new CategoryRepository();

        ImageView btnAddCategory = view.findViewById(R.id.add_category_button);

        btnAddCategory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                CategoryAddDialogFragment dialogFragment = CategoryAddDialogFragment.newInstance(CategoryManagmentFragment.this);
                dialogFragment.show(requireActivity().getSupportFragmentManager(), "CategoryAddDialogFragment");
            }
        });

        loadCategories();
        return view;
    }

    private void setUpCategories() {
        RecyclerView categoryListView = view.findViewById(R.id.categories_view);
        categoryListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        CategoryListAdapter categoryListAdapter = new CategoryListAdapter(categories, requireActivity().getSupportFragmentManager(), CategoryManagmentFragment.this);
        categoryListView.setAdapter(categoryListAdapter);
    }

    public void loadCategories(){
        final List<Category> finalCategories = new ArrayList<>();
        categoryRepository.getAll().thenAccept(categoriesFromDB -> {
            if (categoriesFromDB != null) {
                setUpCategories();
                finalCategories.addAll(categoriesFromDB);
                categories.clear();
                categories.addAll(finalCategories);
            } else {
                Log.e("Failed to retrieve categories", "Failed to retrieve categories from the database");
            }
        });
    }
}