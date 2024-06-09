package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.model.Employee;

import java.util.List;

public class EmployeeCheckboxAdapter extends RecyclerView.Adapter<EmployeeCheckboxAdapter.EmployeeViewHolder>{
    private List<Employee> employees;

    public EmployeeCheckboxAdapter(List<Employee> employees) {
        this.employees = employees;
    }

    @NonNull
    @Override
    public EmployeeCheckboxAdapter.EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_item, parent, false);
        return new EmployeeCheckboxAdapter.EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeCheckboxAdapter.EmployeeViewHolder holder, int position) {
        Employee employee = employees.get(position);
        holder.checkBox.setText(employee.getName() + " " + employee.getSurname());
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_name);
        }
    }
}

