package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryCheckboxAdapter extends RecyclerView.Adapter<CategoryCheckboxAdapter.CategoryViewHolder>{

    private List<Category> categories;
    private List<Category> selectedCategories = new ArrayList<>();

    public CategoryCheckboxAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryCheckboxAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_item, parent, false);
        return new CategoryCheckboxAdapter.CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryCheckboxAdapter.CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.checkBox.setText(category.getName());

        holder.checkBox.setChecked(selectedCategories.contains(category));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedCategories.add(category);
            } else {
                selectedCategories.remove(category);
            }
        });
    }

    public List<Category> getSelectedCategories() {
        return selectedCategories;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_name);
        }
    }

}
