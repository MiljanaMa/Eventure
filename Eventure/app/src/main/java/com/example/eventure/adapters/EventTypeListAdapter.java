package com.example.eventure.adapters;

import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.fragments.admin.EventTypeManagmentFragment;
import com.example.eventure.fragments.admin.dialogs.EventTypeEditDialogFragment;
import com.example.eventure.model.EventType;
import com.example.eventure.model.Subcategory;
import com.example.eventure.repositories.CategoryRepository;
import com.example.eventure.repositories.EventTypeRepository;
import com.example.eventure.repositories.SubcategoryRepository;

import java.util.ArrayList;
import java.util.List;

public class EventTypeListAdapter extends RecyclerView.Adapter<EventTypeListAdapter.EventTypeViewHolder>{


    private List<EventType> eventTypes;
    private FragmentManager fragmentManager;
    private EventTypeManagmentFragment eventTypeFragment;
    public EventTypeListAdapter(List<EventType> eventTypes, FragmentManager fragmentManager, EventTypeManagmentFragment eventTypeManagmentFragment) {
        this.eventTypes = eventTypes;
        this.fragmentManager = fragmentManager;
        this.eventTypeFragment = eventTypeManagmentFragment;
    }

    @NonNull
    @Override
    public EventTypeListAdapter.EventTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_type_card, parent, false);
        return new EventTypeListAdapter.EventTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventTypeListAdapter.EventTypeViewHolder holder, int position) {
        SubcategoryRepository subcategoryRepository = new SubcategoryRepository();
        EventType eventType = eventTypes.get(position);
        holder.eventTypeName.setText(eventType.getName());
        holder.eventTypeDescription.setText(eventType.getDescription());

        List<String> subcategoriesIds = eventType.getSuggestedSubcategoriesIds();
        subcategoryRepository.getAllByIds(subcategoriesIds).thenAccept(subcategoriesFromDB -> {
            if(subcategoriesFromDB != null){
                List<String> subategories = new ArrayList<>();
                for(Subcategory subcategory: subcategoriesFromDB){
                    subategories.add(subcategory.getName());
                }
                String subcategoriesString = TextUtils.join(", ", subategories);
                holder.eventTypeSuggestedSubcategories.setText(subcategoriesString);
            }else{
                holder.eventTypeSuggestedSubcategories.setText("No suggested subcategories");
            }

        });

        holder.editEventTypeButton.setTag(eventType);
        holder.activateEventTypeButton.setTag(eventType);
        String buttonText = eventType.isDeactivated() ? "Activate" : "Deactivate";
        holder.activateEventTypeButton.setText(buttonText);

        holder.activateEventTypeButton.setOnClickListener(v -> {
            EventType selectedEventType = (EventType) v.getTag();
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage("Are you sure you want to deactivate event type named \"" + selectedEventType.getName() +"\" ?")
                    .setTitle("Activate/Deactivate Event Type")
                    .setPositiveButton("YES", (dialog, which) -> {
                        selectedEventType.setDeactivated(!selectedEventType.isDeactivated());
                        EventTypeRepository eventTypeRepository = new EventTypeRepository();
                        eventTypeRepository.update(selectedEventType).thenAccept(eventTypeAdded -> {
                            if(eventTypeAdded){
                                Toast.makeText(v.getContext(), "Event type updated successfully.", Toast.LENGTH_SHORT).show();
                                eventTypeFragment.loadEventTypes();
                            }else{
                                Toast.makeText(v.getContext(), "Failed to update event type.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("NO", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        holder.editEventTypeButton.setOnClickListener(v -> {
            EventType selectedEventType = (EventType) v.getTag();
            EventTypeEditDialogFragment dialogFragment = EventTypeEditDialogFragment.newInstance(selectedEventType, eventTypeFragment);
            dialogFragment.show(fragmentManager, "EventTypeEditDialogFragment");
        });
    }

    @Override
    public int getItemCount() {
        return eventTypes.size();
    }

    public static class EventTypeViewHolder extends RecyclerView.ViewHolder {
        TextView eventTypeName;
        TextView eventTypeDescription;
        TextView eventTypeSuggestedSubcategories;
        ImageView editEventTypeButton;
        Button activateEventTypeButton;

        public EventTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTypeName = itemView.findViewById(R.id.event_type_name);
            eventTypeDescription = itemView.findViewById(R.id.event_type_description);
            eventTypeSuggestedSubcategories = itemView.findViewById(R.id.event_type_suggested_subcategories);
            editEventTypeButton = itemView.findViewById(R.id.edit_event_type_button);
            activateEventTypeButton = itemView.findViewById(R.id.activate_event_type_button);
        }
    }
}
