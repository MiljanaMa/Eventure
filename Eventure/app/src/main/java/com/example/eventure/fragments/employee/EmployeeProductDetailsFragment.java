package com.example.eventure.fragments.employee;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.eventure.R;
import com.example.eventure.databinding.EmployeeProductDetailsFragmentBinding;
import com.example.eventure.model.Product;


public class EmployeeProductDetailsFragment extends Fragment {

    private Product product;
    private EmployeeProductDetailsFragmentBinding binding;

    public EmployeeProductDetailsFragment() {
        // Required empty public constructor
    }

    public static EmployeeProductDetailsFragment newInstance(Product product) {
        EmployeeProductDetailsFragment fragment = new EmployeeProductDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("Product", product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = getArguments().getParcelable("Product");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EmployeeProductDetailsFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView btnGoBack = root.findViewById(R.id.go_back_button);
        btnGoBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(String.valueOf(R.id.employee_products_fragment_container), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        bindFields();

        return root;

    }

    private void bindFields() {
        binding.productCategory.setText(product.getCategoryId());
        binding.productSubcategory.setText(product.getSubcategoryId());
        binding.productName.setText(product.getName());
        binding.productDescription.setText(product.getDescription());
        binding.productPrice.setText(Double.toString(product.getPrice()));
        binding.productDiscount.setText(Double.toString(product.getDiscount()));
        binding.productPriceWithDiscount.setText(Double.toString(product.getPriceWithDiscount()));
        //binding.productEventType.setText(product.getEventTypesIds());
        binding.productAvailable.setText(product.getAvailable().toString());
    }
}