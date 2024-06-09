package com.example.eventure.fragments.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.adapters.SubcategorySuggestionListAdapter;
import com.example.eventure.model.Product;
import com.example.eventure.model.Subcategory;
import com.example.eventure.model.SubcategorySuggestion;
import com.example.eventure.model.enums.SubcategorySuggestionStatus;
import com.example.eventure.model.enums.SubcategoryType;
import com.example.eventure.repositories.SubcategorySuggestionRepository;

import java.util.ArrayList;
import java.util.List;


public class SubcategorySuggestionManagmentFragment extends Fragment {

    private static List<SubcategorySuggestion> suggestions = new ArrayList<>();
    private RecyclerView subcategorySuggestionListView;
    private SubcategorySuggestionListAdapter subcategorySuggestionListAdapter;
    private SubcategorySuggestionRepository suggestionRepository;
    private View view;

    public SubcategorySuggestionManagmentFragment() {
        // Required empty public constructor
    }


    public static SubcategorySuggestionManagmentFragment newInstance() {
        SubcategorySuggestionManagmentFragment fragment = new SubcategorySuggestionManagmentFragment();
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
        view = inflater.inflate(R.layout.subcategory_suggestion_managment_fragment, container, false);
        suggestionRepository = new SubcategorySuggestionRepository();

        loadSuggestions();

        return view;
    }

    private void setUpSuggestions() {
        subcategorySuggestionListView = view.findViewById(R.id.subcategory_suggestions_view);
        subcategorySuggestionListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        subcategorySuggestionListAdapter = new SubcategorySuggestionListAdapter(suggestions, requireActivity().getSupportFragmentManager(), SubcategorySuggestionManagmentFragment.this);
        subcategorySuggestionListView.setAdapter(subcategorySuggestionListAdapter);
    }

    public void loadSuggestions(){
        // JUST TO HAVE SOME SUGGESTION IN DATABASE
/*
        List<String> eventTypesIds = new ArrayList<>();
        eventTypesIds.add("1");

        Subcategory subcategory = new Subcategory("", "Subcategory suggestion 1", "Desc1", "1", SubcategoryType.PRODUCT);
        Product product = new Product("", "1", "1", "Product 1", "desc1", 12.0, 2.0, 2.0, null, eventTypesIds,true, true);
        SubcategorySuggestion suggestion = new SubcategorySuggestion("", "QxhkJUUEKlhcSfd2BjFCwsnTjUH2", subcategory, product, null, SubcategorySuggestionStatus.PENDING);

        suggestionRepository.create(suggestion).thenAccept(suggestionAdded -> {
            if(suggestionAdded){
                Toast.makeText(view.getContext(), "Suggestion added", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(view.getContext(), "Suggestion not added", Toast.LENGTH_SHORT).show();
            }
        });
           */

        final List<SubcategorySuggestion> finalSuggestions = new ArrayList<>();
        suggestionRepository.getAllPending().thenAccept(suggestionsFromDB -> {
            if (suggestionsFromDB != null) {
                setUpSuggestions();
                finalSuggestions.addAll(suggestionsFromDB);
                suggestions.clear();
                suggestions.addAll(finalSuggestions);
            } else {
                Log.e("Failed to retrieve suggestions", "Failed to retrieve suggestions from the database");
            }
        });



    }

}