package com.example.eventure.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.fragments.admin.SubcategoryManagmentFragment;
import com.example.eventure.fragments.admin.dialogs.SubcategoryEditDialogFragment;
import com.example.eventure.model.Subcategory;
import com.example.eventure.repositories.CategoryRepository;
import com.example.eventure.repositories.ProductRepository;
import com.example.eventure.repositories.ServiceRepository;
import com.example.eventure.repositories.SubcategoryRepository;

import java.util.List;

public class SubcategoryListAdapter extends RecyclerView.Adapter<SubcategoryListAdapter.SubcategoryViewHolder>{
    private List<Subcategory> subcategories;
    private FragmentManager fragmentManager;
    private SubcategoryManagmentFragment subcategoryFragment;

    public SubcategoryListAdapter(List<Subcategory> subcategories, FragmentManager fragmentManager, SubcategoryManagmentFragment subcategoryManagmentFragment) {
        this.subcategories = subcategories;
        this.fragmentManager = fragmentManager;
        this.subcategoryFragment = subcategoryManagmentFragment;
    }

    @NonNull
    @Override
    public SubcategoryListAdapter.SubcategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategory_card, parent, false);
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
                    .setPositiveButton("YES", (dialog, which) -> {
                        ProductRepository productRepository = new ProductRepository();
                        productRepository.getAllBySubcategoryId(selectedSubategory.getId()).thenAccept(productsBySubcategoryId -> {
                            if(productsBySubcategoryId == null){
                                ServiceRepository serviceRepository = new ServiceRepository();
                                serviceRepository.getAllBySubcategoryId(selectedSubategory.getId()).thenAccept(servicesBySubcategoryId -> {
                                    if(servicesBySubcategoryId == null){
                                        SubcategoryRepository subcategoryRepository = new SubcategoryRepository();
                                        subcategoryRepository.delete(selectedSubategory.getId()).thenAccept(deleted -> {
                                            if (deleted) {
                                                subcategories.remove(selectedSubategory);
                                                subcategoryFragment.loadSubcategories();
                                                Toast.makeText(v.getContext(), "Subcategory deleted successfully.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(v.getContext(), "Failed to delete subcategory.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else{
                                        Toast.makeText(v.getContext(), "Subcategory cannot be deleted because there are services associated with it", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                Toast.makeText(v.getContext(), "Subcategory cannot be deleted because there are products associated with it", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("NO", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        holder.editSubcategoryButton.setOnClickListener(v -> {
            Subcategory selectedSubategory = (Subcategory) v.getTag();
            SubcategoryEditDialogFragment dialogFragment = SubcategoryEditDialogFragment.newInstance(selectedSubategory, subcategoryFragment);
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
