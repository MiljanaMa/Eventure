package com.example.eventure.fragments.employee;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.eventure.R;
import com.example.eventure.adapters.PackageListAdapter;
import com.example.eventure.model.Package;
import com.example.eventure.model.Product;
import com.example.eventure.model.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EmployeePackagesViewFragment extends Fragment {

    private List<Package> packages;
    private RecyclerView packageListView;
    private PackageListAdapter packageListAdapter;

    public EmployeePackagesViewFragment() {
        // Required empty public constructor
    }


    public static EmployeePackagesViewFragment newInstance() {
        EmployeePackagesViewFragment fragment = new EmployeePackagesViewFragment();
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

        View view = inflater.inflate(R.layout.employee_packages_view_fragment, container, false);

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

        setUpPackages(view);

        return view;

    }

    private void setUpPackages(View view) {
        packageListView = view.findViewById(R.id.packages_view);
        packageListView.setLayoutManager(new LinearLayoutManager(requireContext()));

        packages = new ArrayList<>();
        preparePackages(packages);

        packageListAdapter = new PackageListAdapter(packages, requireActivity().getSupportFragmentManager());
        packageListView.setAdapter(packageListAdapter);
    }


    private void preparePackages(List<Package> packages){

        List<String> subcategories = Arrays.asList("Subcategory 1", "Subcategory 2");
        List<String> eventTypes = Arrays.asList("Event Type 1", "Event Type 2");
        List<String> providers = Arrays.asList("Petar Petrovic", "Marko Markovic");

        Product product1 = new Product("1L", "Category 1", "Subcategory 1", "Product Name 1", "Description 1",
                111.1, 11.1, 88.8, null, eventTypes, true, true);

        Service service1 = new Service("1", "Category 1", "Subcategory 1", "Service Name 1", "Description 1",
                null, "Specifics 1", 11.1, 111.1, 1, "Location 1", 11.1 , providers,
                eventTypes, 1, 1, true, true, true, "1");

        List<Product> products = new ArrayList<>();
        products.add(product1);
        List<Service> services = new ArrayList<>();
        services.add(service1);

        packages.add(new Package("1", "Package name 1", "Description 1", 11.1, true, true, "Category 1",
                subcategories, products, services, eventTypes, 111.1, null, 1, 1, false));
        packages.add(new Package("4", "Package name 2", "Description 2", 22.2, true, true, "Category 1",
                subcategories, products, services, eventTypes, 111.1, null, 2, 2, false));
        packages.add(new Package("3", "Package name 3", "Description 3", 33.3, true, true, "Category 1",
                subcategories, products, services, eventTypes, 111.1, null, 3, 3, false));
        packages.add(new Package("2", "Package name 4", "Description 4", 44.4, true, true, "Category 1",
                subcategories, products, services, eventTypes, 111.1, null, 4, 4, false));

    }

}