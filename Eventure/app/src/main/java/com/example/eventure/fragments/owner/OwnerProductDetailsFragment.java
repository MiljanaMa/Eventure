package com.example.eventure.fragments.owner;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.eventure.R;
import com.example.eventure.databinding.OwnerProductDetailsFragmentBinding;
import com.example.eventure.model.Product;
import com.example.eventure.settings.FragmentTransition;


public class OwnerProductDetailsFragment extends Fragment {

    private Product product;

    private OwnerProductDetailsFragmentBinding binding;

    public OwnerProductDetailsFragment() {
        // Required empty public constructor
    }

    public static OwnerProductDetailsFragment newInstance(Product product) {
        OwnerProductDetailsFragment fragment = new OwnerProductDetailsFragment();
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
        binding = OwnerProductDetailsFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setButtons(root);
        bindFields();

        return root;
    }

    private void setButtons(View root) {
        ImageView btnGoBack = root.findViewById(R.id.go_back_button);
        btnGoBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(String.valueOf(R.id.owner_products_fragment_container), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        ImageView btnEditProduct = root.findViewById(R.id.edit_product_button);
        btnEditProduct.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                ProductEditFormFragment fragment = ProductEditFormFragment.newInstance();
                FragmentTransition.to(fragment, getActivity(),
                        true, R.id.owner_product_details_container);
            }
        });

        ImageView btnDeleteProduct = root.findViewById(R.id.delete_product_button);
        btnDeleteProduct.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Are you sure you want to delete product named \"" + product.getName() +"\" ?")
                        .setTitle("Delete product")
                        .setPositiveButton("YES", null)
                        .setNegativeButton("NO", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
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
        binding.productVisible.setText(product.getVisible().toString());
    }
}