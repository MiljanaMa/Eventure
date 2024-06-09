package com.example.eventure.fragments.common;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.model.Notification;
import com.example.eventure.model.OwnerRegistrationRequest;
import com.example.eventure.model.Time;
import com.example.eventure.model.WorkingHoursRecord;
import com.example.eventure.model.enums.DayOfWeek;
import com.example.eventure.model.enums.NotificationStatus;
import com.example.eventure.repositories.NotificationRepository;
import com.example.eventure.repositories.OwnerRegistrationRequestRepository;
import com.example.eventure.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RegisterOwnerFragment4 extends Fragment {

    OwnerRegistrationRequest request;
    public RegisterOwnerFragment4() {
    }

    public static RegisterOwnerFragment4 newInstance(OwnerRegistrationRequest registrationRequest) {
        RegisterOwnerFragment4 fragment = new RegisterOwnerFragment4();
        Bundle args = new Bundle();
        args.putParcelable("REQUEST", registrationRequest);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            request = getArguments().getParcelable("REQUEST");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_owner_fragment4, container, false);

        TimePicker monStartPicker = view.findViewById(R.id.mondayStartTimePicker);
        monStartPicker.setIs24HourView(true);
        TimePicker monEndPicker = view.findViewById(R.id.mondayEndTimePicker);
        monEndPicker.setIs24HourView(true);
        TimePicker tuesdayStartPicker = view.findViewById(R.id.tuesdayStartTimePicker);
        tuesdayStartPicker.setIs24HourView(true);
        TimePicker tuesdayEndPicker = view.findViewById(R.id.tuesdayEndTimePicker);
        tuesdayEndPicker.setIs24HourView(true);
        TimePicker wednesdayStartPicker = view.findViewById(R.id.wednesdayStartTimePicker);
        wednesdayStartPicker.setIs24HourView(true);
        TimePicker wednesdayEndPicker = view.findViewById(R.id.wednesdayEndTimePicker);
        wednesdayEndPicker.setIs24HourView(true);
        TimePicker thursdayStartPicker = view.findViewById(R.id.thursdayStartTimePicker);
        thursdayStartPicker.setIs24HourView(true);
        TimePicker thursdayEndPicker = view.findViewById(R.id.thursdayEndTimePicker);
        thursdayEndPicker.setIs24HourView(true);
        TimePicker fridayStartPicker = view.findViewById(R.id.fridayStartTimePicker);
        fridayStartPicker.setIs24HourView(true);
        TimePicker fridayEndPicker = view.findViewById(R.id.fridayEndTimePicker);
        fridayEndPicker.setIs24HourView(true);
        TimePicker saturdayStartPicker = view.findViewById(R.id.saturdayStartTimePicker);
        saturdayStartPicker.setIs24HourView(true);
        TimePicker saturdayEndPicker = view.findViewById(R.id.saturdayEndTimePicker);
        saturdayEndPicker.setIs24HourView(true);
        TimePicker sundayStartPicker = view.findViewById(R.id.sundayStartTimePicker);
        sundayStartPicker.setIs24HourView(true);
        TimePicker sundayEndPicker = view.findViewById(R.id.sundayEndTimePicker);
        sundayEndPicker.setIs24HourView(true);

        Button btnNRegisterOwner  = view.findViewById(R.id.register_button);
        btnNRegisterOwner.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                List<WorkingHoursRecord> workingHoursList = new ArrayList<>();
                TimePicker[] startPickers = {
                        monStartPicker, tuesdayStartPicker, wednesdayStartPicker, thursdayStartPicker,
                        fridayStartPicker, saturdayStartPicker, sundayStartPicker
                };
                TimePicker[] endPickers = {
                        monEndPicker, tuesdayEndPicker, wednesdayEndPicker, thursdayEndPicker,
                        fridayEndPicker, saturdayEndPicker, sundayEndPicker
                };

                for (int i = 0; i < 7; i++) {
                    TimePicker startPicker = startPickers[i];
                    TimePicker endPicker = endPickers[i];
                    DayOfWeek dayOfWeek = DayOfWeek.values()[i];
                    Time start = new Time(startPicker.getHour(), startPicker.getMinute());
                    Time end = new Time(endPicker.getHour(), endPicker.getMinute());
                    workingHoursList.add(new WorkingHoursRecord(dayOfWeek, start, end));
                }

                request.getCompany().setWorkingHoursRecords(workingHoursList);

                OwnerRegistrationRequestRepository requestRepository = new OwnerRegistrationRequestRepository();
                requestRepository.create(request).thenAccept(requestAdded -> {
                    if(requestAdded){
                        String title = "New Registration Request";
                        String message = "You have new owner registration request. Please, check it out.";
                        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        NotificationRepository notificationRepository = new NotificationRepository();
                        UserRepository userRepository = new UserRepository();
                        userRepository.getAdminsIds().thenAccept(adminsIds -> {
                            for(String id: adminsIds){
                                Notification notification = new Notification("", title, message, id, currentUserId, NotificationStatus.UNREAD);
                                notificationRepository.create(notification).thenAccept(notificationAdded -> {
                                    if(notificationAdded) {
                                        Toast.makeText(view.getContext(), "Your request is sent", Toast.LENGTH_SHORT).show();
                                        NavHostFragment.findNavController(RegisterOwnerFragment4.this)
                                                .navigate(R.id.action_registerOwnerFragment_to_loginFragment);
                                    }else{
                                        Toast.makeText(view.getContext(), "Failed to send notification.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });

                    } else {
                        Toast.makeText(view.getContext(), "Failed to add request to database.", Toast.LENGTH_SHORT).show();
                    }
                });




            }
        });

        Button btnBack  = view.findViewById(R.id.back_button);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(String.valueOf(R.id.register_owner_container3), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        return view;
    }
}