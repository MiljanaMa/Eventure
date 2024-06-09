package com.example.eventure.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.fragments.admin.dialogs.SubcategoryEditDialogFragment;
import com.example.eventure.model.Subcategory;

import java.util.List;

public class SubcategorybudgetPlanningListAdapter extends RecyclerView.Adapter<SubcategoryListAdapter.SubcategoryViewHolder>{
    private List<Subcategory> subcategories;
    private FragmentManager fragmentManager;

    public SubcategorybudgetPlanningListAdapter(List<Subcategory> subcategories, FragmentManager fragmentManager) {
        this.subcategories = subcategories;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public SubcategoryListAdapter.SubcategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_subcategory_budget_planner_card, parent, false);
        return new SubcategoryListAdapter.SubcategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubcategoryListAdapter.SubcategoryViewHolder holder, int position) {
        Subcategory subcategory = subcategories.get(position);
        holder.subcategoryName.setText(subcategory.getName());
        holder.subcategoryType.setText(subcategory.getType().toString());
        holder.subcategoryDescription.setText(subcategory.getDescription());
        holder.deleteSubcategoryButton.setTag(subcategory);
        holder.editSubcategoryButton.setTag(subcategory);

        holder.deleteSubcategoryButton.setOnClickListener(v -> {
            Subcategory selectedSubategory = (Subcategory) v.getTag();
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage("Are you sure you want to delete subcategory named \"" + selectedSubategory.getName() +"\" ?")
                    .setTitle("Delete subcategory")
                    .setPositiveButton("YES", null)
                    .setNegativeButton("NO", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        holder.editSubcategoryButton.setOnClickListener(v -> {
            Subcategory selectedSubategory = (Subcategory) v.getTag();
            SubcategoryEditDialogFragment dialogFragment = SubcategoryEditDialogFragment.newInstance(selectedSubategory, null);
            dialogFragment.show(fragmentManager, "CategoryEditDialogFragment");
        });

    }

    @Override
    public int getItemCount() {
        return subcategories.size();
    }

    public static class SubcategoryViewHolder extends RecyclerView.ViewHolder {
        TextView subcategoryName;
        TextView subcategoryType;
        TextView subcategoryDescription;
        ImageView deleteSubcategoryButton;
        ImageView editSubcategoryButton;
        public SubcategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            subcategoryName = itemView.findViewById(R.id.subcategory_name);
            subcategoryType = itemView.findViewById(R.id.subcategory_type);
            subcategoryDescription = itemView.findViewById(R.id.subcategory_description);
            deleteSubcategoryButton = itemView.findViewById(R.id.delete_subcategory_button);
            editSubcategoryButton = itemView.findViewById(R.id.edit_subcategory_button);
        }
    }
}
