package com.example.eventure.fragments.organizer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventure.R;
import com.example.eventure.adapters.SubcategorybudgetPlanningListAdapter;
import com.example.eventure.model.Subcategory;
import com.example.eventure.model.enums.SubcategoryType;

import java.util.ArrayList;
import java.util.List;

public class BudgetPlanning1 extends Fragment {

    private EventCreation1 eventCreation1Fragment;
    private List<Subcategory> subcategories;
    private RecyclerView categoryListView;
    private SubcategorybudgetPlanningListAdapter subcategorybudgetPlanningListAdapter;

    public BudgetPlanning1() {
        // Required empty public constructor
    }

    public static BudgetPlanning1 newInstance() {
        BudgetPlanning1 fragment = new BudgetPlanning1();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventCreation1Fragment = new EventCreation1();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget_planning1, container, false);

        /*Button btnSeeSubcategories = view.findViewById(R.id.categorie_linked_to_subcategories); //slececa
        btnSeeSubcategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(BudgetPlanning1.this)
                        .navigate(R.id.action_budgetPlanning1_to_budgetPlanning2);

            }
        }); */

        setUpCategories(view);

        return view;
    }

    private void setUpCategories(View view) {
        categoryListView = view.findViewById(R.id.categorie_linked_to_subcategories);
        categoryListView.setLayoutManager(new LinearLayoutManager(requireContext()));

        //todo: categorie_linked_to_subcategories
        subcategories = new ArrayList<>();
        prepareCategories(subcategories);

        subcategorybudgetPlanningListAdapter = new SubcategorybudgetPlanningListAdapter(subcategories, requireActivity().getSupportFragmentManager());
        categoryListView.setAdapter(subcategorybudgetPlanningListAdapter);
    }

    private void prepareCategories(List<Subcategory> subcategories){

        subcategories.add(new Subcategory("1L", "Torte", "torte za proslave po zelji", "1L", SubcategoryType.PRODUCT));
        subcategories.add(new Subcategory("2L", "Hrana", "hrana za proslave po zelji", "1L", SubcategoryType.PRODUCT));
        subcategories.add(new Subcategory("3L", "Drinks", "pice za proslave po zelji", "1L", SubcategoryType.PRODUCT));
        subcategories.add(new Subcategory("4L", "Fotografisanje", "foto i video zapisi", "1L", SubcategoryType.SERVICE));
    }
}