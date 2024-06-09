package com.example.eventure.fragments.common;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventure.R;
import com.example.eventure.model.Reservation;
import com.example.eventure.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReservationDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservationDetails extends Fragment {

    private static final String ARG_RESERVATION = "param1";
    private Reservation reservation;

    public ReservationDetails() {
        // Required empty public constructor
    }

    public static ReservationDetails newInstance(Reservation reservation) {
        ReservationDetails fragment = new ReservationDetails();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RESERVATION, reservation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reservation = getArguments().getParcelable(ARG_RESERVATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reservation_details, container, false);
    }
}