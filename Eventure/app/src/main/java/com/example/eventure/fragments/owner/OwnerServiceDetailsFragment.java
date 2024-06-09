package com.example.eventure.fragments.owner;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.eventure.R;
import com.example.eventure.databinding.OwnerServiceDetailsFragmentBinding;
import com.example.eventure.model.Service;
import com.example.eventure.settings.FragmentTransition;

import java.util.List;


public class OwnerServiceDetailsFragment extends Fragment {

    private Service service;
    private OwnerServiceDetailsFragmentBinding binding;

    public OwnerServiceDetailsFragment() {
        // Required empty public constructor
    }


    public static OwnerServiceDetailsFragment newInstance(Service service) {
        OwnerServiceDetailsFragment fragment = new OwnerServiceDetailsFragment();
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
        binding = OwnerServiceDetailsFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setButtons(root);
        bindFields(root);

        return root;
    }

    private void setButtons(View root) {
        ImageView btnGoBack = root.findViewById(R.id.go_back_button);
        btnGoBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(String.valueOf(R.id.owner_services_fragment_container), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        ImageView btnEditService = root.findViewById(R.id.edit_service_button);
        btnEditService.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ServiceEditFormFragment fragment = ServiceEditFormFragment.newInstance();
                FragmentTransition.to(fragment, getActivity(),
                        true, R.id.owner_service_details_container);
            }
        });

        ImageView btnDeleteService = root.findViewById(R.id.delete_service_button);
        btnDeleteService.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Are you sure you want to delete service named \"" + service.getName() +"\" ?")
                        .setTitle("Delete service")
                        .setPositiveButton("YES", null)
                        .setNegativeButton("NO", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void bindFields(View root) {

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
        binding.serviceVisible.setText(service.getVisible().toString());
        binding.serviceManualConfirmation.setText(service.getManualConfirmation().toString());
    }

}