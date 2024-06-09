package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.model.Product;

import java.util.List;

public class ProductsInPackageAdapter extends RecyclerView.Adapter<ProductsInPackageAdapter.ProductsInPackageViewHolder>{

    private List<Product> products;
    private FragmentManager fragmentManager;

    public ProductsInPackageAdapter(List<Product> products, FragmentManager fragmentManager) {
        this.products = products;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ProductsInPackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_in_package_card, parent, false);
        return new ProductsInPackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsInPackageViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productName.setText(product.getName());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductsInPackageViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        public ProductsInPackageViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
        }
    }
}