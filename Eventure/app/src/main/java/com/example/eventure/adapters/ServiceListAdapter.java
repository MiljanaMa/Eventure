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
import com.example.eventure.fragments.employee.EmployeeServiceDetailsFragment;
import com.example.eventure.model.Service;
import com.example.eventure.fragments.owner.OwnerServiceDetailsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ServiceViewHolder>{

    private List<Service> services;
    private FragmentManager fragmentManager;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    public ServiceListAdapter(List<Service> services, FragmentManager fragmentManager) {
        this.services = services;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ServiceListAdapter.ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_card, parent, false);
        return new ServiceListAdapter.ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceListAdapter.ServiceViewHolder holder, int position) {
        Service service = services.get(position);
        holder.serviceName.setText(service.getName());
        holder.serviceCategory.setText(service.getCategoryId());
        holder.serviceSubcategory.setText(service.getSubcategoryId());
        holder.servicePrice.setText(Double.toString(service.getFullPrice()));

        holder.serviceCard.setOnClickListener(v -> {
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
                                    EmployeeServiceDetailsFragment fragment = EmployeeServiceDetailsFragment.newInstance(service);
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.employee_services_fragment_container, fragment);
                                    fragmentTransaction.addToBackStack(String.valueOf(R.id.employee_services_fragment_container));
                                    fragmentTransaction.commit();
                                }else{
                                    OwnerServiceDetailsFragment fragment = OwnerServiceDetailsFragment.newInstance(service);
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.owner_services_fragment_container, fragment);
                                    fragmentTransaction.addToBackStack(String.valueOf(R.id.owner_services_fragment_container));
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
        return services.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName;
        TextView serviceCategory;
        TextView serviceSubcategory;
        TextView servicePrice;
        LinearLayout serviceCard;
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.service_name);
            serviceCategory = itemView.findViewById(R.id.service_category);
            serviceSubcategory = itemView.findViewById(R.id.service_subcategory);
            servicePrice = itemView.findViewById(R.id.service_price);
            serviceCard = itemView.findViewById(R.id.service_card);
        }
    }
}
