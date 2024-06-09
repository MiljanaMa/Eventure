package com.example.eventure.fragments.owner;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.eventure.R;
import com.example.eventure.adapters.EventTypesCheckboxAdapter;
import com.example.eventure.adapters.ProductsCheckboxAdapter;
import com.example.eventure.adapters.ServicesCheckboxAdapter;
import com.example.eventure.model.EventType;
import com.example.eventure.model.Product;
import com.example.eventure.model.Service;

import java.util.ArrayList;
import java.util.List;


public class PackageAddFormFragment extends Fragment {
    private static final int GET_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private List<EventType> eventTypes;
    private RecyclerView eventTypesCheckboxView;
    private EventTypesCheckboxAdapter eventTypesCheckboxAdapter;
    private List<Product> products;
    private RecyclerView productsCheckboxView;
    private ProductsCheckboxAdapter productsCheckboxAdapter;
    private List<Service> services;
    private RecyclerView servicesCheckboxView;
    private ServicesCheckboxAdapter serviceCheckboxAdapter;

    public PackageAddFormFragment() {
        // Required empty public constructor
    }


    public static PackageAddFormFragment newInstance() {
        PackageAddFormFragment fragment = new PackageAddFormFragment();
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
        View view = inflater.inflate(R.layout.package_add_form_fragment, container, false);

        setButtons(view);
        setUpProducts(view);
        setUpServices(view);
        setUpEventTypes(view);
        return view;
    }

    private void setButtons(View view) {

        Button btnUploadImage  = view.findViewById(R.id.upload_image_button);
        imageView = view.findViewById(R.id.imageView);

        btnUploadImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GET_IMAGE_REQUEST);
            }
        });

        Button btnCancel = view.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(String.valueOf(R.id.owner_packages_fragment_container), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        Button btnAddPackage = view.findViewById(R.id.package_add_button);
        btnAddPackage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(String.valueOf(R.id.owner_packages_fragment_container), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
    }

    private void setUpProducts(View view) {
        productsCheckboxView = view.findViewById(R.id.products_checkbox_view);
        productsCheckboxView.setLayoutManager(new LinearLayoutManager(requireContext()));

        products = new ArrayList<>();
        prepareProducts(products);

        productsCheckboxAdapter = new ProductsCheckboxAdapter(products);
        productsCheckboxView.setAdapter(productsCheckboxAdapter);
    }

    private void setUpServices(View view) {
        servicesCheckboxView = view.findViewById(R.id.services_checkbox_view);
        servicesCheckboxView.setLayoutManager(new LinearLayoutManager(requireContext()));

        services = new ArrayList<>();
        prepareServices(services);

        serviceCheckboxAdapter = new ServicesCheckboxAdapter(services);
        servicesCheckboxView.setAdapter(serviceCheckboxAdapter);
    }

    private void setUpEventTypes(View view) {
        eventTypesCheckboxView = view.findViewById(R.id.events_types_checkbox_view);
        eventTypesCheckboxView.setLayoutManager(new LinearLayoutManager(requireContext()));

        eventTypes = new ArrayList<>();
        prepareEventTypes(eventTypes);

        eventTypesCheckboxAdapter = new EventTypesCheckboxAdapter(eventTypes);
        eventTypesCheckboxView.setAdapter(eventTypesCheckboxAdapter);
    }

    private void prepareProducts(List<Product> products){
        List<String> eventTypeIds = new ArrayList<>();
        eventTypeIds.add("1");

        products.add(new Product("1L", "Category 1", "Subcategory 1", "Product Name 1", "Description 1",
                111.1, 11.1, 88.8, null, eventTypeIds, true, true));

        products.add(new Product("2L", "Category 2", "Subcategory 2", "Product Name 2", "Description 2",
                222.2, 22.2, 155.5, null, eventTypeIds, true, true));

        products.add(new Product("3L", "Category 3", "Subcategory 3", "Product Name 3", "Description 3",
                333.3, 33.3, 222.2, null, eventTypeIds, true, true));

        products.add(new Product("4L", "Category 1", "Subcategory 1", "Product Name 4", "Description 1",
                444.4, 44.4, 222.2, null, eventTypeIds, true, true));
    }

    private void prepareServices(List<Service> services){
        List<String> eventTypeIds = new ArrayList<>();
        eventTypeIds.add("1");

        services.add(new Service("1", "Category 1", "Subcategory 1", "Service Name 1", "Description 1",
                null, "Specifics 1", 11.1, 111.1, 1, "Location 1", 11.1 , null,
                eventTypeIds, 1, 1, true, true, true, "1"));

        services.add(new Service("2", "Category 2", "Subcategory 2", "Service Name 2", "Description 2",
                null, "Specifics 2", 22.2, 222.2, 2, "Location 2", 22.2 , null,
                eventTypeIds, 2, 2, true, true, true, "1"));

        services.add(new Service("3", "Category 3", "Subcategory 3", "Service Name 3", "Description 3",
                null, "Specifics 3", 33.3, 333.3, 3, "Location 3", 33.3 , null,
                eventTypeIds, 3, 3, true, true, true, "1"));

        services.add(new Service("4", "Category 4", "Subcategory 4", "Service Name 4", "Description 4",
                null, "Specifics 4", 44.4, 444.4, 4, "Location 4", 44.4, null,
                eventTypeIds, 4, 4, true, true, true, "1"));
    }


    private void prepareEventTypes(List<EventType> eventTypes){

        eventTypes.add(new EventType("1L", "Event Type 1", "Description 1", null, false));
        eventTypes.add(new EventType("2L", "Event Type 2", "Description 2", null, false));
        eventTypes.add(new EventType("3L", "Event Type 3", "Description 3", null, false));
        eventTypes.add(new EventType("4L", "Event Type 4", "Description 4", null, false));
    }
}