package com.example.eventure.fragments.common;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.eventure.R;
import com.example.eventure.adapters.NotificationListAdapter;
import com.example.eventure.model.Notification;
import com.example.eventure.model.enums.NotificationStatus;
import com.example.eventure.repositories.NotificationRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment {

    private static final List<Notification> notifications = new ArrayList<>();
    private View view;
    private NotificationRepository notificationRepository;
    private int selectedSortOption = 0;

    public NotificationFragment() {
    }

    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.notification_fragment, container, false);
        notificationRepository = new NotificationRepository();
        Spinner statusSpinner = view.findViewById(R.id.status_spinner);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSortOption = position;
                loadNotifications();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        loadNotifications();

        SensorManager sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometerSensor != null) {
            sensorManager.registerListener(sensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        return view;
    }

    private void setUpNotifications() {
        RecyclerView notificationsListView = view.findViewById(R.id.notifications_view);
        notificationsListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        NotificationListAdapter notificationsListAdapter = new NotificationListAdapter(notifications, requireActivity().getSupportFragmentManager(), NotificationFragment.this);
        notificationsListView.setAdapter(notificationsListAdapter);
    }

    public void loadNotifications() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            final List<Notification> finalNotifications = new ArrayList<>();
            notificationRepository.getAllByReceiverId(currentUser.getUid()).thenAccept(notificationsFromDB -> {
                if (notificationsFromDB != null) {
                    filterNotifications(notificationsFromDB);
                    setUpNotifications();
                } else {
                    Log.e("Notifications fragment", "Failed to retrieve notifications from the database");
                }
            });
        }
    }

    private void filterNotifications(List<Notification> notificationsFromDB) {
        List<Notification> filteredNotifications = new ArrayList<>();

        switch (selectedSortOption) {
            case 0:
                // All notifications
                filteredNotifications.addAll(notificationsFromDB);
                break;
            case 1:
                // Read notifications
                for (Notification notification : notificationsFromDB) {
                    if (notification.getStatus() == NotificationStatus.READ) {
                        filteredNotifications.add(notification);
                    }
                }
                break;
            case 2:
                // Unread notifications
                for (Notification notification : notificationsFromDB) {
                    if (notification.getStatus() == NotificationStatus.UNREAD) {
                        filteredNotifications.add(notification);
                    }
                }
                break;
            default:
                break;
        }
        notifications.clear();
        notifications.addAll(filteredNotifications);
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        private static final float SHAKE_THRESHOLD = 10f;
        private long lastShakeTime = 0;

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                long currentTime = System.currentTimeMillis();
                if ((currentTime - lastShakeTime) > 1000) {
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];
                    double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;
                    if (acceleration > SHAKE_THRESHOLD) {
                        rotateNotificationView();
                        lastShakeTime = currentTime;
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void rotateNotificationView() {
        selectedSortOption++;
        if (selectedSortOption > 2) {
            selectedSortOption = 0;
        }
        Spinner statusSpinner = view.findViewById(R.id.status_spinner);
        statusSpinner.setSelection(selectedSortOption);
        loadNotifications();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SensorManager sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(sensorListener);
    }

}