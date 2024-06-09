package com.example.eventure.fragments.organizer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.eventure.R;
import com.example.eventure.fragments.common.LoginFragment;

public class EventCreation1 extends Fragment {

    /*
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    */
    private LoginFragment loginFragment;

    public EventCreation1() {
    }


    public static EventCreation1 newInstance() {
        EventCreation1 fragment = new EventCreation1();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loginFragment = new LoginFragment();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_creation1, container, false);

        Button nextButton = view.findViewById(R.id.finish_event_creation);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NavHostFragment.findNavController(EventCreation1.this)
                //        .navigate(R.id.action_eventCreationFragment1_to_organizerMainFragment);

            }
        });

        Button planBudgetButton = view.findViewById(R.id.budget_planning_categories_container);
        planBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(EventCreation1.this)
                        .navigate(R.id.action_eventCreationFragment1_to_budgetPlanningFragment1);
            }
        });


        return view;
    }

}