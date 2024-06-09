package com.example.eventure.fragments.common;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.eventure.R;
import com.example.eventure.adapters.EmployeeAdapter;
import com.example.eventure.adapters.RatingAdapter;
import com.example.eventure.adapters.ReservationsAdapter;
import com.example.eventure.model.Offer;
import com.example.eventure.model.Rating;
import com.example.eventure.model.Reservation;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.UserRole;
import com.example.eventure.repositories.ReservationRepository;
import com.example.eventure.repositories.ServiceRepository;
import com.example.eventure.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Reservations#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Reservations extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private FirebaseUser currentUser;
    private User user;
    private UserRepository userRepository;
    private ReservationRepository reservationRepository;
    private ServiceRepository serviceRepository;
    private RecyclerView recyclerView;
    private View mainView;
    private List<Reservation> reservations;
    private List<Reservation> allReservations;
    private List<Reservation> statusReservations;

    public Reservations() {
        // Required empty public constructor
    }

    public static Reservations newInstance(String param1, String param2) {
        Reservations fragment = new Reservations();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_reservations, container, false);
        userRepository = new UserRepository();
        reservationRepository = new ReservationRepository();
        serviceRepository = new ServiceRepository();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        userRepository.getByUID(currentUser.getUid()).thenAccept(userDb -> {
            if (userDb != null) {
                user = userDb;
                loadReservations(mainView);
                Spinner typeSpinner = mainView.findViewById(R.id.reservation_status_spinner);
                typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        applyFilters();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                    }
                });
                Button showSearchButton = mainView.findViewById(R.id.showSearchReservationButton);
                if (!user.getRole().equals(UserRole.ORGANIZER)) {
                    showSearchButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //set field visibilities
                            if (user.getRole().equals(UserRole.EMPLOYEE)) {
                                LinearLayout name = mainView.findViewById(R.id.employeeName);
                                name.setVisibility(View.GONE);
                                LinearLayout surname = mainView.findViewById(R.id.employeeSurname);
                                surname.setVisibility(View.GONE);
                            }

                            View searchFields = mainView.findViewById(R.id.searchReservationFields);
                            if (searchFields.getVisibility() == View.VISIBLE)
                                searchFields.setVisibility(View.GONE);
                            else
                                searchFields.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    showSearchButton.setVisibility(View.GONE);
                }
            }

        });

        Button search = mainView.findViewById(R.id.searchReservationsButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(mainView);
            }
        });
        return mainView;
    }

    private void applyFilters() {
        AppCompatSpinner statusSpinner = mainView.findViewById(R.id.reservation_status_spinner);

        String selectedStatus = statusSpinner.getSelectedItem().toString();
        String updatedStatus = selectedStatus.replace(" ", "_");

        List<Reservation> filteredReservations = new ArrayList<>();
        for (Reservation reservation : allReservations) {
            String status = reservation.getStatus().toString();
            if (updatedStatus.equals("ALL") || status.equalsIgnoreCase(updatedStatus)) {
                filteredReservations.add(reservation);
            }
        }
        statusReservations = filteredReservations;
        search(mainView);
    }

    private void search(View view) {
        String organizerName = ((EditText) view.findViewById(R.id.searchOrganizerName)).getText().toString().trim();
        String organizerSurname = ((EditText) view.findViewById(R.id.searchOrganizerSurname)).getText().toString().trim();
        String employeeName = "";
        String employeeSurname = "";

        if (user.getRole().equals(UserRole.OWNER)){
            employeeName = ((EditText) view.findViewById(R.id.searchEmployeeName)).getText().toString().trim();
            employeeSurname = ((EditText) view.findViewById(R.id.searchEmployeeSurname)).getText().toString().trim();
        }
        String service = ((EditText) view.findViewById(R.id.searchServiceName)).getText().toString().trim();

        List<Reservation> ret = new ArrayList<>();
        for (Reservation r : statusReservations) {
            if (r.getService().getName().toLowerCase().contains(service) && r.getOrganizer().getFirstName().toLowerCase().contains(organizerName) && r.getOrganizer().getLastName().toLowerCase().contains(organizerSurname)) {
                if (user.getRole().equals(UserRole.OWNER)){
                    if(r.getEmployee().getFirstName().toLowerCase().contains(employeeName) && r.getEmployee().getLastName().toLowerCase().contains(employeeSurname)){
                        ret.add(r);
                    }
                }else
                    ret.add(r);
            }
        }
        reservations = ret;
        initializeRecyclerView(mainView);
    }

    private void initializeRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.reservationsRecyclerView);
        ReservationsAdapter reservationsAdapter = new ReservationsAdapter(reservations, requireActivity().getSupportFragmentManager());
        recyclerView.setAdapter(reservationsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void loadReservations(View view) {
        if (user.getRole() == UserRole.OWNER) {
            reservationRepository.getAllByCompany(user.getCompanyId()).thenAccept(reservationsDb -> {
                if (reservationsDb != null) {
                    allReservations = setReservationFields(reservationsDb);
                    statusReservations = allReservations;
                    reservations = reservationsDb;
                    initializeRecyclerView(view);
                }
            });
        } else if (user.getRole() == UserRole.EMPLOYEE) {
            reservationRepository.getByEmployee(user.getId()).thenAccept(reservationsDb -> {
                allReservations = setReservationFields(reservationsDb);
                statusReservations = allReservations;
                reservations = reservationsDb;
                initializeRecyclerView(view);
            });
        } else if (user.getRole() == UserRole.ORGANIZER) {
            reservationRepository.getByOrganizer(user.getId()).thenAccept(reservationsDb -> {
                allReservations = setReservationFields(reservationsDb);
                statusReservations = allReservations;
                reservations = reservationsDb;
                initializeRecyclerView(view);
            });
        }
    }
    private List<Reservation> setReservationFields(List<Reservation> reservationsDb){
        List<Reservation> reservationsCopy = new ArrayList<>();
        for(Reservation reservation: reservationsDb){
            Reservation reservationCopy = new Reservation(reservation);
            userRepository.getByUID(reservation.getOrganizerId()).thenAccept(organizer ->{
                if(organizer != null){
                    reservationCopy.setOrganizer(organizer);
                    userRepository.getByUID(reservation.getEmployeeId()).thenAccept(employee -> {
                        if(employee != null){
                            reservationCopy.setEmployee(employee);
                            serviceRepository.getByUID(reservation.getServiceId()).thenAccept(service -> {
                                if(service != null){
                                    reservationCopy.setService(service);
                                    reservationsCopy.add(reservationCopy);
                                }
                            });
                        }
                    });
                }
            });
        }
        return reservationsCopy;
    }
}