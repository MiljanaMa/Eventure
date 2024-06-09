package com.example.eventure.fragments.organizer;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventure.R;
import com.example.eventure.adapters.EventListAdapter;
import com.example.eventure.model.Event;
import com.example.eventure.repositories.EventRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventsListing#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsListing extends Fragment {

    private static final List<Event> events = new ArrayList<>();
    private RecyclerView eventListView;
    private EventListAdapter eventListAdapter;
    private static EventRepository eventRepository;
    private View view;


    public EventsListing() {
        // Required empty public constructor
    }


    public static EventsListing newInstance() {
        EventsListing fragment = new EventsListing();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventRepository = new EventRepository();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_listing, container, false);
        eventListView = view.findViewById(R.id.event_listing_card);
        eventListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadEvents();
        eventListAdapter = new EventListAdapter(events, requireActivity().getSupportFragmentManager());
        eventListAdapter.setEvents(events);
        eventListView.setAdapter(eventListAdapter);

        return view;
    }

    private void setUpData(List<Event> events) {
        eventListAdapter.notifyDataSetChanged();
    }

    private void loadEvents(){
        final List<Event> loadedEvents = new ArrayList<>();
        eventRepository.getAll().thenAccept(eventsFromDB -> {
            if (eventsFromDB != null) {
                loadedEvents.addAll(eventsFromDB);
                events.clear();
                events.addAll(loadedEvents);
                setUpData(events);
                Log.e("\n retrieved events\n", events.toString());
            } else {
                Log.e("Failed to retrieve events", "Failed to retrieve events from the database");
            }
        });
    }

}