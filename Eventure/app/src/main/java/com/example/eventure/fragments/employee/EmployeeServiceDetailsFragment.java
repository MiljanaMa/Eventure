package com.example.eventure.fragments.employee;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.eventure.R;
import com.example.eventure.databinding.EmployeeServiceDetailsFragmentBinding;
import com.example.eventure.model.Service;

import java.util.List;


public class EmployeeServiceDetailsFragment extends Fragment {

    private Service service;
    private EmployeeServiceDetailsFragmentBinding binding;

    public EmployeeServiceDetailsFragment() {
        // Required empty public constructor
    }

    public static EmployeeServiceDetailsFragment newInstance(Service service) {
        EmployeeServiceDetailsFragment fragment = new EmployeeServiceDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("Service", service);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            service = getArguments().getParcelable("Service");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = EmployeeServiceDetailsFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView btnGoBack = root.findViewById(R.id.go_back_button);
        btnGoBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(String.valueOf(R.id.employee_services_fragment_container), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        bindFields();

        return root;
    }

    private void bindFields() {
        List<String> providers = service.getServiceProviders();
        StringBuilder providersSB = new StringBuilder();
        for (int i = 0; i < providers.size(); i++) {
            providersSB.append(providers.get(i));
            if (i < providers.size() - 1) {
                providersSB.append(", ");
            }
        }

        binding.serviceCategory.setText(service.getCategoryId());
        binding.serviceSubcategory.setText(service.getSubcategoryId());
        binding.serviceName.setText(service.getName());
        binding.serviceDescription.setText(service.getDescription());
        binding.serviceSpecifics.setText(service.getSpecifics());
        binding.servicePricePerHour.setText(Double.toString(service.getPricePerHour()));
        binding.serviceFullPrice.setText(Double.toString(service.getFullPrice()));
        binding.serviceDuration.setText(Double.toString(service.getDuration()));
        binding.serviceLocation.setText(service.getLocation());
        binding.serviceDiscount.setText(Double.toString(service.getDiscount()));
        binding.serviceProviders.setText(providersSB.toString());
        //binding.serviceEventType.setText(service.getEventTypeId());
        binding.serviceReservationDeadline.setText(Integer.toString(service.getReservationDeadlineInDays()));
        binding.serviceCancellationDeadline.setText(Integer.toString(service.getCancellationDeadlineInDays()));
        binding.serviceAvailable.setText(service.getAvailable().toString());
    }
}