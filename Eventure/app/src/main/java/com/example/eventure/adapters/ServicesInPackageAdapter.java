package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.model.Product;
import com.example.eventure.model.Service;

import java.util.List;

public class ServicesInPackageAdapter extends RecyclerView.Adapter<ServicesInPackageAdapter.ServicesInPackageViewHolder>{

    private List<Service> services;
    private FragmentManager fragmentManager;

    public ServicesInPackageAdapter(List<Service> services, FragmentManager fragmentManager) {
        this.services = services;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ServicesInPackageAdapter.ServicesInPackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_in_package_card, parent, false);
        return new ServicesInPackageAdapter.ServicesInPackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicesInPackageAdapter.ServicesInPackageViewHolder holder, int position) {
        Service service = services.get(position);
        holder.serviceName.setText(service.getName());
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ServicesInPackageViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName;
        public ServicesInPackageViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.service_name);
        }
    }
}
