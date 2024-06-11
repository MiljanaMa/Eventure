package com.example.eventure.fragments.organizer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventure.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatOverviewFragment extends Fragment {


    public ChatOverviewFragment() {
        // Required empty public constructor
    }


    public static ChatOverviewFragment newInstance(String param1, String param2) {
        ChatOverviewFragment fragment = new ChatOverviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_list, container, false);
    }
}