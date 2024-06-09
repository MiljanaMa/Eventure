package com.example.eventure.fragments.owner;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.eventure.R;
import com.example.eventure.adapters.ServiceListAdapter;
import com.example.eventure.model.Service;
import com.example.eventure.settings.FragmentTransition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class OwnerServicesViewFragment extends Fragment {

    private List<Service> services;
    private RecyclerView serviceListView;
    private ServiceListAdapter serviceListAdapter;

    public OwnerServicesViewFragment() {
        // Required empty public constructor
    }


    public static OwnerServicesViewFragment newInstance() {
        OwnerServicesViewFragment fragment = new OwnerServicesViewFragment();
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

        View view = inflater.inflate(R.layout.owner_services_view_fragment, container, false);

        setButtons(view);
        setUpServices(view);

        return view;
    }

    private void setButtons(View view) {
        ImageView btnAddService = view.findViewById(R.id.add_service_button);
        btnAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceAddFormFragment fragment = ServiceAddFormFragment.newInstance();
                FragmentTransition.to(fragment, getActivity(),
                        true, R.id.owner_services_fragment_container);
            }
        });

        Button btnFilter = view.findViewById(R.id.filter_button);
        btnFilter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(view.findViewById(R.id.filter_container).getVisibility() == View.VISIBLE){
                    view.findViewById(R.id.filter_container).setVisibility(View.GONE);
                }else{
                    view.findViewById(R.id.filter_container).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setUpServices(View view) {
        serviceListView = view.findViewById(R.id.services_view);
        serviceListView.setLayoutManager(new LinearLayoutManager(requireContext()));

        services = new ArrayList<>();
        prepareServices(services);

        serviceListAdapter = new ServiceListAdapter(services, requireActivity().getSupportFragmentManager());
        serviceListView.setAdapter(serviceListAdapter);
    }

    private void prepareServices(List<Service> services){

        List<String> providers = Arrays.asList("Petar Petrovic", "Marko Markovic");
        List<String> eventTypeIds = new ArrayList<>();
        eventTypeIds.add("1");

        services.add(new Service("1", "Category 1", "Subcategory 1", "Service Name 1", "Description 1",
                null, "Specifics 1", 11.1, 111.1, 1, "Location 1", 11.1 , providers,
                eventTypeIds, 1, 1, true, true, true, "1"));

        services.add(new Service("2", "Category 2", "Subcategory 2", "Service Name 2", "Description 2",
                null, "Specifics 2", 22.2, 222.2, 2, "Location 2", 22.2 , providers,
                eventTypeIds, 2, 2, true, true, true, "1"));

        services.add(new Service("3", "Category 3", "Subcategory 3", "Service Name 3", "Description 3",
                null, "Specifics 3", 33.3, 333.3, 3, "Location 3", 33.3 , providers,
                eventTypeIds, 3, 3, true, true, true, "1"));

        services.add(new Service("4", "Category 4", "Subcategory 4", "Service Name 4", "Description 4",
                null, "Specifics 4", 44.4, 444.4, 4, "Location 4", 44.4, providers,
                eventTypeIds, 4, 4, true, true, true, "1"));
    }
}