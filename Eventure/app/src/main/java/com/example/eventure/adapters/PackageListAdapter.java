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
import com.example.eventure.fragments.employee.EmployeePackageDetailsFragment;
import com.example.eventure.model.Package;
import com.example.eventure.fragments.owner.OwnerPackageDetailsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PackageListAdapter extends RecyclerView.Adapter<PackageListAdapter.PackageViewHolder>{

    private List<Package> packages;
    private FragmentManager fragmentManager;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    public PackageListAdapter(List<Package> packages, FragmentManager fragmentManager) {
        this.packages = packages;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public PackageListAdapter.PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_card, parent, false);
        return new PackageListAdapter.PackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageListAdapter.PackageViewHolder holder, int position) {
        Package packageItem = packages.get(position);
        holder.packageName.setText(packageItem.getName());
        holder.packageCategory.setText(packageItem.getCategory());
        holder.packageSubcategory.setText(packageItem.getSubcategories().toString());
        holder.packagePrice.setText(Double.toString(packageItem.getPrice()));

        holder.packageCard.setOnClickListener(v -> {

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
                                    EmployeePackageDetailsFragment fragment = EmployeePackageDetailsFragment.newInstance(packageItem);
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.employee_packages_fragment_container, fragment);
                                    fragmentTransaction.addToBackStack(String.valueOf(R.id.employee_packages_fragment_container));
                                    fragmentTransaction.commit();
                                }else{
                                    OwnerPackageDetailsFragment fragment = OwnerPackageDetailsFragment.newInstance(packageItem);
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.owner_packages_fragment_container, fragment);
                                    fragmentTransaction.addToBackStack(String.valueOf(R.id.owner_packages_fragment_container));
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
        return packages.size();
    }

    public static class PackageViewHolder extends RecyclerView.ViewHolder {
        TextView packageName;
        TextView packageCategory;
        TextView packageSubcategory;
        TextView packagePrice;
        LinearLayout packageCard;
        public PackageViewHolder(@NonNull View itemView) {
            super(itemView);
            packageName = itemView.findViewById(R.id.package_name);
            packageCategory = itemView.findViewById(R.id.package_category);
            packageSubcategory = itemView.findViewById(R.id.package_subcategory);
            packagePrice = itemView.findViewById(R.id.package_price);
            packageCard = itemView.findViewById(R.id.package_card);
        }
    }
}
