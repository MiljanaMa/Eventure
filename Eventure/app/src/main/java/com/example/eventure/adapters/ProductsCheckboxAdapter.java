package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.model.Product;

import java.util.List;

public class ProductsCheckboxAdapter extends RecyclerView.Adapter<ProductsCheckboxAdapter.ProductViewHolder>{

    private List<Product> products;

    public ProductsCheckboxAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductsCheckboxAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_item, parent, false);
        return new ProductsCheckboxAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsCheckboxAdapter.ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.checkBox.setText(product.getName());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_name);
        }
    }
}
