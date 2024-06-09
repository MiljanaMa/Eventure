package com.example.eventure.fragments.owner;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.eventure.R;
import com.example.eventure.adapters.ProductsInPackageAdapter;
import com.example.eventure.adapters.ServicesInPackageAdapter;
import com.example.eventure.databinding.OwnerPackageDetailsFragmentBinding;
import com.example.eventure.model.Package;
import com.example.eventure.model.Product;
import com.example.eventure.model.Service;
import com.example.eventure.settings.FragmentTransition;

import java.util.ArrayList;
import java.util.List;

public class OwnerPackageDetailsFragment extends Fragment {

    private Package packageItem;
    private OwnerPackageDetailsFragmentBinding binding;
    private List<Product> products;
    private RecyclerView productListView;
    private ProductsInPackageAdapter productsInPackageAdapter;
    private List<Service> services;
    private RecyclerView serviceListView;
    private ServicesInPackageAdapter servicesInPackageAdapter;


    public OwnerPackageDetailsFragment() {
        // Required empty public constructor
    }

    public static OwnerPackageDetailsFragment newInstance(Package packageItem) {
        OwnerPackageDetailsFragment fragment = new OwnerPackageDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("Package", packageItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            packageItem = getArguments().getParcelable("Package");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = OwnerPackageDetailsFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setButtons(root);
        setUpProducts(root);
        setUpServices(root);
        bindFields();

        return root;
    }
    private void setButtons(View root) {
        ImageView btnGoBack = root.findViewById(R.id.go_back_button);
        btnGoBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(String.valueOf(R.id.owner_packages_fragment_container), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        ImageView btnEditProduct = root.findViewById(R.id.edit_package_button);
        btnEditProduct.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                PackageEditFormFragment fragment = PackageEditFormFragment.newInstance();
                FragmentTransition.to(fragment, getActivity(),
                        true, R.id.owner_package_details_container);
            }
        });

        ImageView btnDeleteProduct = root.findViewById(R.id.delete_package_button);
        btnDeleteProduct.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Are you sure you want to delete package named \"" + packageItem.getName() +"\" ?")
                        .setTitle("Delete package")
                        .setPositiveButton("YES", null)
                        .setNegativeButton("NO", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void setUpProducts(View view) {
        productListView = view.findViewById(R.id.products_view);
        productListView.setLayoutManager(new LinearLayoutManager(requireContext()));

        products = new ArrayList<>();
        prepareProducts(products);

        productsInPackageAdapter = new ProductsInPackageAdapter(products, requireActivity().getSupportFragmentManager());
        productListView.setAdapter(productsInPackageAdapter);
    }

    private void prepareProducts(List<Product> products){
        List<String> eventTypeIds = new ArrayList<>();
        eventTypeIds.add("1");

        products.add(new Product("1L", "Category 1", "Subcategory 1", "Product Name 1", "Description 1",
                111.1, 11.1, 88.8, null, eventTypeIds, true, true));

        products.add(new Product("2L", "Category 2", "Subcategory 2", "Product Name 2", "Description 2",
                222.2, 22.2, 155.5, null, eventTypeIds, true, true));

    }

    private void setUpServices(View view) {
        serviceListView = view.findViewById(R.id.services_view);
        serviceListView.setLayoutManager(new LinearLayoutManager(requireContext()));

        services = new ArrayList<>();
        prepareServices(services);

        servicesInPackageAdapter = new ServicesInPackageAdapter(services, requireActivity().getSupportFragmentManager());
        serviceListView.setAdapter(servicesInPackageAdapter);
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

    }

    private void bindFields() {

        List<String> categories = packageItem.getSubcategories();
        StringBuilder categoriesSB = new StringBuilder();
        for (int i = 0; i < categories.size(); i++) {
            categoriesSB.append(categories.get(i));
            if (i < categories.size() - 1) {
                categoriesSB.append(", ");
            }
        }

        List<String> eventTypes = packageItem.getEventTypes();
        StringBuilder eventTypesSB = new StringBuilder();
        for (int i = 0; i < eventTypes.size(); i++) {
            eventTypesSB.append(eventTypes.get(i));
            if (i < eventTypes.size() - 1) {
                eventTypesSB.append(", ");
            }
        }



        binding.packageName.setText(packageItem.getName());
        binding.packageDescription.setText(packageItem.getDescription());
        binding.packageDiscount.setText(Double.toString(packageItem.getDiscount()));
        binding.packageCategory.setText(packageItem.getCategory());
        binding.packageSubcategory.setText(categoriesSB.toString());
        binding.packageEventType.setText(eventTypesSB.toString());
        binding.packagePrice.setText(Double.toString(packageItem.getPrice()));
        binding.packageReservationDeadline.setText(Integer.toString(packageItem.getReservationDeadlineInDays()));
        binding.packageCancellationDeadline.setText(Integer.toString(packageItem.getCancellationDeadlineInDays()));
        binding.packageAvailable.setText(packageItem.getAvailable().toString());
        binding.packageVisible.setText(packageItem.getVisible().toString());
        binding.packageManualConfirmation.setText(packageItem.getManualConfirmation().toString());
    }
}