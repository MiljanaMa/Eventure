package com.example.eventure.fragments.admin.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.databinding.RegistrationRequestDetailsFragmentBinding;
import com.example.eventure.model.Category;
import com.example.eventure.model.OwnerRegistrationRequest;
import com.example.eventure.repositories.CategoryRepository;
import com.example.eventure.repositories.EventTypeRepository;

import java.util.List;

public class RegistrationRequestDetailsFragment extends DialogFragment {

    private RegistrationRequestDetailsFragmentBinding binding;
    private static OwnerRegistrationRequest request;

    public RegistrationRequestDetailsFragment() {
    }

    public static RegistrationRequestDetailsFragment newInstance(OwnerRegistrationRequest selectedRequest) {
        RegistrationRequestDetailsFragment fragment = new RegistrationRequestDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("REQUEST", selectedRequest);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            request = getArguments().getParcelable("REQUEST");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = RegistrationRequestDetailsFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button btnBack = root.findViewById(R.id.back_button);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            binding.ownerEmail.setText(request.getOwner().getEmail());
            binding.ownerFirstName.setText(request.getOwner().getFirstName());
            binding.ownerLastName.setText(request.getOwner().getLastName());
            binding.ownerAddress.setText(request.getOwner().getAddress());
            binding.ownerPhone.setText(request.getOwner().getPhone());
            binding.companyEmail.setText(request.getCompany().getEmail());
            binding.companyName.setText(request.getCompany().getName());
            binding.companyAddress.setText(request.getCompany().getAddress());
            binding.companyPhone.setText(request.getCompany().getPhone());
            binding.companyDescription.setText(request.getCompany().getDescription());


            CategoryRepository categoryRepository = new CategoryRepository();
            categoryRepository.getAllByIds(request.getCompany().getCategoriesIds()).thenAccept(categoriesFromDB -> {
                if(categoriesFromDB!=null) {
                    StringBuilder categoriesSB = new StringBuilder();
                    for (int i = 0; i < categoriesFromDB.size(); i++) {
                        categoriesSB.append(categoriesFromDB.get(i));
                        if (i < categoriesFromDB.size() - 1) {
                            categoriesSB.append(", ");
                        }
                    }
                    binding.categories.setText(categoriesSB.toString());

                }

            });

            EventTypeRepository eventTypeRepository = new EventTypeRepository();
            eventTypeRepository.getAllByIds(request.getCompany().getEventTypesIds()).thenAccept(eventTypesFromDB -> {
                if(eventTypesFromDB!=null) {
                    StringBuilder eventTypesSB = new StringBuilder();
                    for (int i = 0; i < eventTypesFromDB.size(); i++) {
                        eventTypesSB.append(eventTypesFromDB.get(i));
                        if (i < eventTypesFromDB.size() - 1) {
                            eventTypesSB.append(", ");
                        }
                    }
                    binding.eventTypes.setText(eventTypesSB.toString());

                }

            });

        }

        return root;
    }
}