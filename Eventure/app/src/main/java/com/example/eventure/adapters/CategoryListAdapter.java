package com.example.eventure.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.fragments.admin.CategoryManagmentFragment;
import com.example.eventure.fragments.admin.SubcategoryManagmentFragment;
import com.example.eventure.fragments.admin.dialogs.CategoryEditDialogFragment;
import com.example.eventure.model.Category;
import com.example.eventure.repositories.CategoryRepository;
import com.example.eventure.repositories.ProductRepository;
import com.example.eventure.repositories.ServiceRepository;

import java.util.List;

public class CategoryListAdapter  extends RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>{

    private List<Category> categories;
    private FragmentManager fragmentManager;
    private CategoryManagmentFragment categoryFragment;
    public CategoryListAdapter(List<Category> categories, FragmentManager fragmentManager, CategoryManagmentFragment categoryManagmentFragment) {
        this.categories = categories;
        this.fragmentManager = fragmentManager;
        this.categoryFragment = categoryManagmentFragment;
    }

    @NonNull
    @Override
    public CategoryListAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card, parent, false);
        return new CategoryListAdapter.CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListAdapter.CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryName.setText(category.getName());
        holder.categoryDescription.setText(category.getDescription());
        holder.manageSubcategoriesButton.setTag(category);
        holder.deleteCategoryButton.setTag(category);
        holder.editCategoryButton.setTag(category);

        holder.manageSubcategoriesButton.setOnClickListener(v -> {

            Category selectedCategory = (Category) v.getTag();
            SubcategoryManagmentFragment fragment = SubcategoryManagmentFragment.newInstance(selectedCategory.getId());
            FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.categories_fragment_container, fragment);
            transaction.addToBackStack(String.valueOf(R.id.categories_fragment_container));
            transaction.commit();

        });

        holder.deleteCategoryButton.setOnClickListener(v -> {
            Category selectedCategory = (Category) v.getTag();
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage("Are you sure you want to delete category named \"" + selectedCategory.getName() + "\" ?")
                    .setTitle("Delete category")
                    .setPositiveButton("YES", (dialog, which) -> {
                        ProductRepository productRepository = new ProductRepository();
                        productRepository.getAllByCategoryId(selectedCategory.getId()).thenAccept(productsByCategoryId -> {
                            if(productsByCategoryId == null){
                                ServiceRepository serviceRepository = new ServiceRepository();
                                serviceRepository.getAllByCategoryId(selectedCategory.getId()).thenAccept(servicesByCategoryId -> {
                                   if(servicesByCategoryId == null){
                                       CategoryRepository categoryRepository = new CategoryRepository();
                                       categoryRepository.delete(selectedCategory.getId()).thenAccept(deleted -> {
                                           if (deleted) {
                                               categories.remove(selectedCategory);
                                               categoryFragment.loadCategories();
                                               Toast.makeText(v.getContext(), "Category deleted successfully.", Toast.LENGTH_SHORT).show();
                                           } else {
                                               Toast.makeText(v.getContext(), "Failed to delete category.", Toast.LENGTH_SHORT).show();
                                           }
                                       });
                                   } else{
                                       Toast.makeText(v.getContext(), "Category cannot be deleted because there are services associated with it", Toast.LENGTH_SHORT).show();
                                   }
                                });
                            }else{
                                Toast.makeText(v.getContext(), "Category cannot be deleted because there are products associated with it", Toast.LENGTH_SHORT).show();
                            }
                        });

                    })
                    .setNegativeButton("NO", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        holder.editCategoryButton.setOnClickListener(v -> {
            Category selectedCategory = (Category) v.getTag();
            CategoryEditDialogFragment dialogFragment = CategoryEditDialogFragment.newInstance(selectedCategory, categoryFragment);
            dialogFragment.show(fragmentManager, "CategoryEditDialogFragment");
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        TextView categoryDescription;
        Button manageSubcategoriesButton;
        ImageView deleteCategoryButton;
        ImageView editCategoryButton;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryDescription = itemView.findViewById(R.id.category_description);
            manageSubcategoriesButton = itemView.findViewById(R.id.manage_subcategories_button);
            deleteCategoryButton = itemView.findViewById(R.id.delete_category_button);
            editCategoryButton = itemView.findViewById(R.id.edit_category_button);
        }
    }

}
