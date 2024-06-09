package com.example.eventure.adapters;

import android.app.AlertDialog;
import android.graphics.Color;
import android.media.audiofx.NoiseSuppressor;
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
import com.example.eventure.fragments.admin.SubcategoryManagmentFragment;
import com.example.eventure.fragments.admin.dialogs.SubcategoryEditDialogFragment;
import com.example.eventure.fragments.common.NotificationFragment;
import com.example.eventure.model.EventType;
import com.example.eventure.model.Notification;
import com.example.eventure.model.Subcategory;
import com.example.eventure.model.enums.NotificationStatus;
import com.example.eventure.repositories.EventTypeRepository;
import com.example.eventure.repositories.NotificationRepository;
import com.example.eventure.repositories.ProductRepository;
import com.example.eventure.repositories.ServiceRepository;
import com.example.eventure.repositories.SubcategoryRepository;

import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder>{

    private List<Notification> notifications;
    private FragmentManager fragmentManager;
    private NotificationFragment notificationFragment;

    public NotificationListAdapter(List<Notification> notifications, FragmentManager fragmentManager, NotificationFragment notificationFragment) {
        this.notifications = notifications;
        this.fragmentManager = fragmentManager;
        this.notificationFragment = notificationFragment;
    }

    @NonNull
    @Override
    public NotificationListAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card, parent, false);
        return new NotificationListAdapter.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationListAdapter.NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.title.setText(notification.getTitle());
        holder.message.setText(notification.getMessage());
        holder.status.setText(notification.getStatus().toString());
        if (notification.getStatus().equals(NotificationStatus.UNREAD)) {
            holder.markAsReadButton.setVisibility(View.VISIBLE);
            holder.status.setTextColor(Color.parseColor("#FF0000"));
        } else {
            holder.markAsReadButton.setVisibility(View.INVISIBLE);
            holder.status.setTextColor(Color.parseColor("#00FF00"));
        }

        holder.markAsReadButton.setTag(notification);
        holder.markAsReadButton.setOnClickListener(v -> {
            Notification selectedNotification = (Notification) v.getTag();
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage("Are you sure you want to mark this notification as read?")
                    .setTitle("Mark notification as read")
                    .setPositiveButton("YES", (dialog, which) -> {
                        selectedNotification.setStatus(NotificationStatus.READ);
                        NotificationRepository notificationRepository = new NotificationRepository();
                        notificationRepository.update(selectedNotification).thenAccept(notificationUpdated -> {
                            if(notificationUpdated){
                                Toast.makeText(v.getContext(), "Notification updated successfully.", Toast.LENGTH_SHORT).show();
                                notificationFragment.loadNotifications();
                            }else{
                                Toast.makeText(v.getContext(), "Failed to update notification.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("NO", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView message;
        TextView status;
        Button markAsReadButton;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            message = itemView.findViewById(R.id.message);
            status = itemView.findViewById(R.id.status);
            markAsReadButton = itemView.findViewById(R.id.mark_as_read_button);
        }
    }

}
