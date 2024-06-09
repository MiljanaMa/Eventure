package com.example.eventure.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventure.R;
import com.example.eventure.model.Appointment;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>{
    private List<Appointment> appointments;

    public EventAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //fix here
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
        return new EventAdapter.EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.bind(appointment);
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView textEventName;
        private TextView textEventDay;
        private TextView textEventTimeFrom;
        private TextView textEventTimeTo;
        private TextView textEventStatus;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            textEventDay = (TextView) itemView.findViewById(R.id.textEventDay);
            textEventName = (TextView) itemView.findViewById(R.id.textEventName);
            textEventTimeFrom = (TextView) itemView.findViewById(R.id.textEventTimeFrom);
            textEventTimeTo = (TextView) itemView.findViewById(R.id.textEventTimeTo);
            textEventStatus = (TextView) itemView.findViewById(R.id.textEventStatus);
        }

        public void bind(Appointment appointment) {
            textEventDay.setText(appointment.getDate().toString());
            textEventName.setText(appointment.getName());
            textEventTimeFrom.setText(appointment.getFrom().toString());
            textEventTimeTo.setText(appointment.getTo().toString());
            textEventStatus.setText(appointment.getStatus().toString());

        }
    }
}
