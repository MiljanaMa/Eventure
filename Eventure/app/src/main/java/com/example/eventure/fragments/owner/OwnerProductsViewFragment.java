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
import com.example.eventure.adapters.ProductListAdapter;
import com.example.eventure.model.Product;
import com.example.eventure.settings.FragmentTransition;

import java.util.ArrayList;
import java.util.List;


public class OwnerProductsViewFragment extends Fragment {

    private List<Product> products;
    private RecyclerView productListView;
    private ProductListAdapter productListAdapter;

    public OwnerProductsViewFragment() {
        // Required empty public constructor
    }

    public static OwnerProductsViewFragment newInstance() {
        OwnerProductsViewFragment fragment = new OwnerProductsViewFragment();
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

        View view = inflater.inflate(R.layout.owner_products_view_fragment, container, false);

        setButtons(view);
        setUpProducts(view);

        return view;
    }

    private void setButtons(View view) {
        ImageView btnAddProduct = view.findViewById(R.id.add_product_button);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductAddFormFragment fragment = ProductAddFormFragment.newInstance();
                FragmentTransition.to(fragment, getActivity(),
                        true, R.id.owner_products_fragment_container);

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

    private void setUpProducts(View view) {
        productListView = view.findViewById(R.id.products_view);
        productListView.setLayoutManager(new LinearLayoutManager(requireContext()));

        products = new ArrayList<>();
        prepareProducts(products);

        productListAdapter = new ProductListAdapter(products, requireActivity().getSupportFragmentManager());
        productListView.setAdapter(productListAdapter);
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

        products.add(new Product("4L", "Category 4", "Subcategory 1", "Product Name 4", "Description 1",
                444.4, 44.4, 222.2, null, eventTypeIds, true, true));
    }

}