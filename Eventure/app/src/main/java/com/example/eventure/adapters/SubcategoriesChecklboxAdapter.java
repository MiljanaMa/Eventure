package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.model.Category;
import com.example.eventure.model.Subcategory;

import java.util.ArrayList;
import java.util.List;

public class SubcategoriesChecklboxAdapter extends RecyclerView.Adapter<SubcategoriesChecklboxAdapter.SubcategoriesViewHolder>{

    private List<Subcategory> subcategories;
    private List<Subcategory> selectedSubcategories = new ArrayList<>();

    public SubcategoriesChecklboxAdapter(List<Subcategory> subcategories) {
        this.subcategories = subcategories;
    }

    @NonNull
    @Override
    public SubcategoriesChecklboxAdapter.SubcategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_item, parent, false);
        return new SubcategoriesChecklboxAdapter.SubcategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubcategoriesChecklboxAdapter.SubcategoriesViewHolder holder, int position) {
        Subcategory subcategory = subcategories.get(position);
        holder.checkBox.setText(subcategory.getName());

        holder.checkBox.setChecked(selectedSubcategories.contains(subcategory));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedSubcategories.add(subcategory);
            } else {
                selectedSubcategories.remove(subcategory);
            }
        });
    }

    public List<Subcategory> getSelectedSubcategories() {
        return selectedSubcategories;
    }

    public void setCheckedSubcategories(List<String> suggestedSubcategoriesIds) {
        for (String subcategoryId : suggestedSubcategoriesIds) {
            for (Subcategory subcategory : subcategories) {
                if (subcategory.getId().equals(subcategoryId)) {
                    selectedSubcategories.add(subcategory);
                    break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return subcategories.size();
    }

    public static class SubcategoriesViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        public SubcategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_name);
        }
    }
}
