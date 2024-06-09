package com.example.eventure.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.model.Event;
import com.example.eventure.model.Offer;

import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {
    private List<Event> events;
    private FragmentManager fragmentManager;

    public EventListAdapter(List<Event> events, FragmentManager fragmentManager) {
        this.events = events;
        this.fragmentManager = fragmentManager;
    }

    public void setEvents(List<Event> events){
        this.events = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventListAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_listing_card, parent, false);
        Log.d("event listing adapter 1", "event adapter 1");

        return new EventListAdapter.EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventListAdapter.EventViewHolder holder, int position) {
        Event event = events.get(position);
        if (holder.eventName != null) {
            holder.eventName.setText(event.getName());
        }
        if (holder.eventDescription != null) {
            holder.eventDescription.setText(event.getDescription());
        }
        if (holder.eventLocation != null) {
            holder.eventLocation.setText(event.getLocationRestrictions());
        }
        if (holder.eventPrivacyRules != null) {
            holder.eventPrivacyRules.setText(event.getPrivacyRules());
        }
        if (holder.eventType != null) {
            holder.eventType.setText(event.getEventType());
        }
        Log.d("event listing adapter 2", "event adapter 2");

        //prikaz detalja ili cega vec
    }


    @Override
    public int getItemCount() {
        return events.size();
    }


    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        TextView eventDescription;
        TextView eventPrivacyRules;
        TextView eventLocation;

        TextView eventType;
        LinearLayout eventCard;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDescription = itemView.findViewById(R.id.event_description);
            eventPrivacyRules = itemView.findViewById(R.id.event_privacy_rules);
            eventLocation = itemView.findViewById(R.id.event_location);
            eventType = itemView.findViewById(R.id.event_type);
            eventCard = itemView.findViewById(R.id.event_listing_card);
            Log.d("event listing adapter 3", "event adapter 3");

        }
    }

}
