package com.example.eventure.fragments.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.eventure.R;
import com.example.eventure.adapters.EventTypeListAdapter;
import com.example.eventure.fragments.admin.dialogs.EventTypeAddDialogFragment;
import com.example.eventure.model.Category;
import com.example.eventure.model.EventType;
import com.example.eventure.repositories.EventTypeRepository;

import java.util.ArrayList;
import java.util.List;

public class EventTypeManagmentFragment extends Fragment {

    private static final List<EventType> eventTypes = new ArrayList<>();
    private static EventTypeRepository eventTypeRepository;
    private View view;

    public EventTypeManagmentFragment() {

    }


    public static EventTypeManagmentFragment newInstance(String param1, String param2) {
        EventTypeManagmentFragment fragment = new EventTypeManagmentFragment();
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

        view = inflater.inflate(R.layout.event_type_managment_fragment, container, false);
        eventTypeRepository = new EventTypeRepository();
        ImageView btnAddEventType = view.findViewById(R.id.add_event_type_button);

        btnAddEventType.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EventTypeAddDialogFragment dialogFragment = EventTypeAddDialogFragment.newInstance(EventTypeManagmentFragment.this);
                dialogFragment.show(requireActivity().getSupportFragmentManager(), "EventTypeAddDialogFragment");
            }
        });

        loadEventTypes();
        return view;
    }

    private void setUpEventTypes() {
        RecyclerView eventTypeListView = view.findViewById(R.id.event_types_view);
        eventTypeListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        EventTypeListAdapter eventTypeListAdapter = new EventTypeListAdapter(eventTypes, requireActivity().getSupportFragmentManager(), EventTypeManagmentFragment.this);
        eventTypeListView.setAdapter(eventTypeListAdapter);
    }

    public void loadEventTypes(){
        final List<EventType> finalEventTypes = new ArrayList<>();
        eventTypeRepository.getAll().thenAccept(eventTypesFromDB -> {
            if (eventTypesFromDB != null) {
                setUpEventTypes();
                finalEventTypes.addAll(eventTypesFromDB);
                eventTypes.clear();
                eventTypes.addAll(finalEventTypes);
            } else {
                Log.e("Failed to retrieve event types", "Failed to retrieve event types from the database");
            }
        });
    }
}