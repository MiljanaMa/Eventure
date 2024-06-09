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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.fragments.admin.SubcategorySuggestionManagmentFragment;
import com.example.eventure.fragments.admin.dialogs.SubcategorySuggestionAddToExistingDialogFragment;
import com.example.eventure.fragments.admin.dialogs.SubcategorySuggestionEditDialogFragment;
import com.example.eventure.model.Product;
import com.example.eventure.model.SubcategorySuggestion;
import com.example.eventure.model.enums.SubcategorySuggestionStatus;
import com.example.eventure.repositories.CategoryRepository;
import com.example.eventure.repositories.ProductRepository;
import com.example.eventure.repositories.SubcategoryRepository;
import com.example.eventure.repositories.SubcategorySuggestionRepository;

import java.util.List;

public class SubcategorySuggestionListAdapter extends RecyclerView.Adapter<SubcategorySuggestionListAdapter.SubcategorySuggestionViewHolder>{
    private List<SubcategorySuggestion> subcategorySuggestions;
    private FragmentManager fragmentManager;
    private SubcategorySuggestionManagmentFragment suggestionFragment;

    public SubcategorySuggestionListAdapter(List<SubcategorySuggestion> subcategorySuggestions, FragmentManager fragmentManager, SubcategorySuggestionManagmentFragment subcategorySuggestionFragment) {
        this.subcategorySuggestions = subcategorySuggestions;
        this.fragmentManager = fragmentManager;
        this.suggestionFragment = subcategorySuggestionFragment;
    }

    @NonNull
    @Override
    public SubcategorySuggestionListAdapter.SubcategorySuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategory_suggestion_card, parent, false);
        return new SubcategorySuggestionListAdapter.SubcategorySuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubcategorySuggestionListAdapter.SubcategorySuggestionViewHolder holder, int position) {
        SubcategorySuggestion subcategorySuggestion = subcategorySuggestions.get(position);
        holder.subcategorySuggestionName.setText(subcategorySuggestion.getSubcategory().getName());
        holder.subcategorySuggestionType.setText(subcategorySuggestion.getSubcategory().getType().toString());

        CategoryRepository categoryRepository = new CategoryRepository();
        categoryRepository.getByUID(subcategorySuggestion.getSubcategory().getCategoryId()).thenAccept(category -> {
            if(category != null){
                holder.subcategorySuggestionCategory.setText(category.getName());
            }
        });

        holder.subcategorySuggestionCategory.setText(subcategorySuggestion.getSubcategory().getCategoryId());
        holder.subcategorySuggestionDescription.setText(subcategorySuggestion.getSubcategory().getDescription());
        holder.editSubcategorySuggestionButton.setTag(subcategorySuggestion);
        holder.addToExistingSubcategoryButton.setTag(subcategorySuggestion);
        holder.acceptSubcategorySuggestionButton.setTag(subcategorySuggestion);


        holder.editSubcategorySuggestionButton.setOnClickListener(v -> {
            SubcategorySuggestion selectedSubcategorySuggestion= (SubcategorySuggestion) v.getTag();
            SubcategorySuggestionEditDialogFragment dialogFragment = SubcategorySuggestionEditDialogFragment.newInstance(selectedSubcategorySuggestion, suggestionFragment);
            dialogFragment.show(fragmentManager, "SubcategorySuggestionEditDialogFragment");
        });

        holder.addToExistingSubcategoryButton.setOnClickListener(v -> {
            SubcategorySuggestion selectedSubcategorySuggestion= (SubcategorySuggestion) v.getTag();
            SubcategorySuggestionAddToExistingDialogFragment dialogFragment = SubcategorySuggestionAddToExistingDialogFragment.newInstance(selectedSubcategorySuggestion, suggestionFragment);
            dialogFragment.show(fragmentManager, "SubcategorySuggestionEditDialogFragment");
        });

        holder.acceptSubcategorySuggestionButton.setOnClickListener(v -> {
            SubcategorySuggestion selectedSuggestion = (SubcategorySuggestion) v.getTag();
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage("Are you sure you want to accept subcategory suggestion named \"" + selectedSuggestion.getSubcategory().getName() +"\" ?")
                    .setTitle("Accept subcategory suggestion")
                    .setPositiveButton("YES", (dialog, which) -> {
                        SubcategoryRepository subcategoryRepository = new SubcategoryRepository();
                        subcategoryRepository.create(selectedSuggestion.getSubcategory()).thenAccept(subcategoryId -> {
                            if(subcategoryId != null){
                                selectedSuggestion.getProduct().setSubcategoryId(subcategoryId);
                                ProductRepository productRepository = new ProductRepository();
                                productRepository.create(selectedSuggestion.getProduct()).thenAccept(productAdded -> {
                                    if(productAdded){
                                        selectedSuggestion.setStatus(SubcategorySuggestionStatus.ACCEPTED);
                                        SubcategorySuggestionRepository suggestionRepository = new SubcategorySuggestionRepository();
                                        suggestionRepository.update(selectedSuggestion).thenAccept(suggestionUpdated -> {
                                            if(suggestionUpdated){
                                                Toast.makeText(v.getContext(), "Suggestion accepted successfully.", Toast.LENGTH_SHORT).show();
                                                suggestionFragment.loadSuggestions();
                                            }else{
                                                Toast.makeText(v.getContext(), "Failed to accept suggestion.", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                });
                            }
                        });
                    })
                    .setNegativeButton("NO", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

    }

    @Override
    public int getItemCount() {
        return subcategorySuggestions.size();
    }

    public static class SubcategorySuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView subcategorySuggestionName;
        TextView subcategorySuggestionCategory;
        TextView subcategorySuggestionType;
        TextView subcategorySuggestionDescription;
        ImageView editSubcategorySuggestionButton;
        Button addToExistingSubcategoryButton;
        ImageView acceptSubcategorySuggestionButton;
        public SubcategorySuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            subcategorySuggestionName = itemView.findViewById(R.id.subcategory_suggestion_name);
            subcategorySuggestionCategory = itemView.findViewById(R.id.subcategory_suggestion_category);
            subcategorySuggestionType = itemView.findViewById(R.id.subcategory_suggestion_type);
            subcategorySuggestionDescription = itemView.findViewById(R.id.subcategory_suggestion_description);
            editSubcategorySuggestionButton = itemView.findViewById(R.id.edit_subcategory_suggestion_button);
            addToExistingSubcategoryButton = itemView.findViewById(R.id.add_to_existing_subcategory_button);
            acceptSubcategorySuggestionButton= itemView.findViewById(R.id.accept_subcategory_suggestion_button);
        }
    }
}
