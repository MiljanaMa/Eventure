package com.example.eventure.fragments.common;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventure.R;
import com.example.eventure.activities.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;


public class LogoutFragment extends Fragment {

    public LogoutFragment() {
        // Required empty public constructor
    }

    public static LogoutFragment newInstance(String param1, String param2) {
        LogoutFragment fragment = new LogoutFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth.getInstance().signOut();
        ((HomeActivity) requireActivity()).updateMenuVisibility();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        NavHostFragment.findNavController(LogoutFragment.this)
                .navigate(R.id.action_logoutFragment_to_loginFragment);

        return inflater.inflate(R.layout.logout_fragment, container, false);
    }
}