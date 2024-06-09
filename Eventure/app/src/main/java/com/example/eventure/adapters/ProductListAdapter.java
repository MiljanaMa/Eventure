package com.example.eventure.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.fragments.employee.EmployeeProductDetailsFragment;
import com.example.eventure.model.Product;
import com.example.eventure.fragments.owner.OwnerProductDetailsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>{

    private List<Product> products;
    private FragmentManager fragmentManager;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    public ProductListAdapter(List<Product> products, FragmentManager fragmentManager) {
        this.products = products;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ProductListAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
        return new ProductListAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListAdapter.ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productName.setText(product.getName());
        holder.productCategory.setText(product.getCategoryId());
        holder.productSubcategory.setText(product.getSubcategoryId());
        holder.productPrice.setText(Double.toString(product.getPrice()));

        holder.productCard.setOnClickListener(v -> {

            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userInfo = db.collection("users").document(currentUser.getUid());

            userInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String roleString = document.getString("role");
                            if (roleString != null) {
                                if(roleString.equals("EMPLOYEE")){
                                    EmployeeProductDetailsFragment fragment = EmployeeProductDetailsFragment.newInstance(product);
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.employee_products_fragment_container, fragment);
                                    fragmentTransaction.addToBackStack(String.valueOf(R.id.employee_products_fragment_container));
                                    fragmentTransaction.commit();
                                }else{
                                    OwnerProductDetailsFragment fragment = OwnerProductDetailsFragment.newInstance(product);
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.owner_products_fragment_container, fragment);
                                    fragmentTransaction.addToBackStack(String.valueOf(R.id.owner_products_fragment_container));
                                    fragmentTransaction.commit();
                                }
                            }
                        } else {
                            Log.d("No document", "No such document");
                        }
                    } else {
                        Log.d("Exception", "get failed with ", task.getException());
                    }
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productCategory;
        TextView productSubcategory;
        TextView productPrice;
        LinearLayout productCard;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productCategory = itemView.findViewById(R.id.product_category);
            productSubcategory = itemView.findViewById(R.id.product_subcategory);
            productPrice = itemView.findViewById(R.id.product_price);
            productCard = itemView.findViewById(R.id.product_card);
        }
    }
}
