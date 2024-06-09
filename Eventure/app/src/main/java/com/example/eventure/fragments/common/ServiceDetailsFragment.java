package com.example.eventure.fragments.common;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.fragments.organizer.dialogs.ServiceReservationDialogFragment;
import com.example.eventure.model.Favorites;
import com.example.eventure.model.Notification;
import com.example.eventure.model.Service;
import com.example.eventure.model.enums.NotificationStatus;
import com.example.eventure.repositories.FavoritesRepository;
import com.example.eventure.repositories.NotificationRepository;
import com.example.eventure.utils.UUIDUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ServiceDetailsFragment extends Fragment {

    private static final String ARG_SERVICE = "service";
    private Service service;
    private FirebaseUser currentUser;


    public static ServiceDetailsFragment newInstance(Service service) {
        ServiceDetailsFragment fragment = new ServiceDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SERVICE, service);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            service = getArguments().getParcelable(ARG_SERVICE);
        }
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service_details, container, false);
    }*/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_service_details, container, false);

        Button reserveButton = view.findViewById(R.id.reserve_button);
        reserveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ServiceReservationDialogFragment dialogFragment = ServiceReservationDialogFragment.newInstance(service);
                dialogFragment.show(getFragmentManager(), "ServiceReservationDialogFragment");
            }
        });

        List<String> providers = service.getServiceProviders();
        StringBuilder providersSB = new StringBuilder();
        for (int i = 0; i < providers.size(); i++) {
            providersSB.append(providers.get(i));
            if (i < providers.size() - 1) {
                providersSB.append(", ");
            }
        }
        if (service != null) {

            TextView serviceCategory = view.findViewById(R.id.service_category);
            TextView serviceSubcategory = view.findViewById(R.id.service_subcategory);
            TextView serviceName = view.findViewById(R.id.service_name);
            TextView serviceDescription = view.findViewById(R.id.service_description);
            TextView serviceSpecifics = view.findViewById(R.id.service_specifics);
            TextView servicePricePerHour = view.findViewById(R.id.service_price_per_hour);
            TextView serviceFullPrice = view.findViewById(R.id.service_full_price);
            TextView serviceDuration = view.findViewById(R.id.service_duration);
            TextView serviceLocation = view.findViewById(R.id.service_location);
            TextView serviceDiscount = view.findViewById(R.id.service_discount);
            TextView serviceProviders = view.findViewById(R.id.service_providers);
            //TextView serviceEventType = view.findViewById(R.id.service_event_type);
            TextView serviceReservationDeadline = view.findViewById(R.id.service_reservation_deadline);
            TextView serviceCancellationDeadline = view.findViewById(R.id.service_cancellation_deadline);
            TextView serviceAvailable = view.findViewById(R.id.service_available);
            TextView serviceVisible = view.findViewById(R.id.service_visible);
            TextView serviceManualConfirmation = view.findViewById(R.id.service_manual_confirmation);
            Button showCompanyProfile = view.findViewById(R.id.showCompanyProfile);

            showCompanyProfile.setOnClickListener(v -> {
                CompanyProfile companyProfile = CompanyProfile.newInstance(service.getCompanyId());
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_service_details, companyProfile);
                transaction.addToBackStack(String.valueOf(R.id.fragment_service_details));
                transaction.commit();
            });

            serviceCategory.setText(service.getCategoryId());
            serviceSubcategory.setText(service.getSubcategoryId());
            serviceName.setText(service.getName());
            serviceDescription.setText(service.getDescription());
            serviceSpecifics.setText(service.getSpecifics());
            servicePricePerHour.setText(Double.toString(service.getPricePerHour()));
            serviceFullPrice.setText(Double.toString(service.getFullPrice()));
            serviceDuration.setText(Double.toString(service.getDuration()));
            serviceLocation.setText(service.getLocation());
            serviceDiscount.setText(Double.toString(service.getDiscount()));
            serviceProviders.setText(providersSB.toString());
            //serviceEventType.setText(service.getEventTypeId());
            serviceReservationDeadline.setText(Integer.toString(service.getReservationDeadlineInDays()));
            serviceCancellationDeadline.setText(Integer.toString(service.getCancellationDeadlineInDays()));
            serviceAvailable.setText(service.getAvailable() != null && service.getAvailable() ? "Yes" : "No");
            serviceVisible.setText(service.getVisible() != null && service.getVisible() ? "Yes" : "No");
            serviceManualConfirmation.setText(service.getManualConfirmation() != null && service.getManualConfirmation() ? "Yes" : "No");

        }

        Button addToFavoritesBtn = view.findViewById(R.id.addToFavorites);
        addToFavoritesBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Favorites newFavorite = new Favorites();
                newFavorite.setName(service.getName());
                newFavorite.setDescription(service.getDescription());
                newFavorite.setItemId(service.getId());
                newFavorite.setItemType("service");

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
                newFavorite.setOrganizerId(currentUser.getUid());

                FavoritesRepository favoritesRepository = new FavoritesRepository();
                NotificationRepository notificationRepository = new NotificationRepository();

                favoritesRepository.create(newFavorite).thenAccept(createdFavorite -> {
                    if(createdFavorite != null){
                        Toast.makeText(view.getContext(), "New service added to favorites list.", Toast.LENGTH_SHORT).show();
                        Notification notification = new Notification(UUIDUtil.generateUUID(), "New favorite", "New service added to favorites. Check its details and reserve it!", currentUser.getUid(), currentUser.getUid(), NotificationStatus.UNREAD);
                        notificationRepository.create(notification);
                    } else {
                        Toast.makeText(view.getContext(), "Failed to add new service to favorites.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }
}