package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.model.Category;
import com.example.eventure.model.EventType;

import java.util.ArrayList;
import java.util.List;

public class EventTypesCheckboxAdapter extends RecyclerView.Adapter<EventTypesCheckboxAdapter.EventTypeViewHolder>{
    private List<EventType> eventTypes;
    private List<EventType> selectedEventTypes = new ArrayList<>();

    public EventTypesCheckboxAdapter(List<EventType> eventTypes) {
        this.eventTypes = eventTypes;
    }

    @NonNull
    @Override
    public EventTypesCheckboxAdapter.EventTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_item, parent, false);
        return new EventTypesCheckboxAdapter.EventTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventTypesCheckboxAdapter.EventTypeViewHolder holder, int position) {
        EventType eventType = eventTypes.get(position);
        holder.checkBox.setText(eventType.getName());

        holder.checkBox.setChecked(selectedEventTypes.contains(eventType));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedEventTypes.add(eventType);
            } else {
                selectedEventTypes.remove(eventType);
            }
        });
    }

    public List<EventType> getSelectedEventTypes() {
        return selectedEventTypes;
    }

    @Override
    public int getItemCount() {
        return eventTypes.size();
    }

    public static class EventTypeViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        public EventTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_name);
        }
    }
}
