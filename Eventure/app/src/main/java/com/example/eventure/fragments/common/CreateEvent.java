package com.example.eventure.fragments.common;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.model.Appointment;
import com.example.eventure.model.Company;
import com.example.eventure.model.Date;
import com.example.eventure.model.Employee;
import com.example.eventure.model.EmployeeWorkingHours;
import com.example.eventure.model.Time;
import com.example.eventure.model.User;
import com.example.eventure.model.WorkingHoursRecord;
import com.example.eventure.model.enums.AppointmentStatus;
import com.example.eventure.model.enums.DayOfWeek;
import com.example.eventure.repositories.AppointmentRepository;
import com.example.eventure.repositories.CompanyRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.utils.UUIDUtil;
import com.example.eventure.utils.ImageUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateEvent extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String fromFragment;
    private String employeeId;
    private static Time fromTime;
    private static Time toTime;
    private static Date date;
    private User employee;
    private Company company;
    private AppointmentRepository appointmentRepository;
    private CompanyRepository companyRepository;
    private UserRepository userRepository;
    private List<Appointment> appointmentList;

    public CreateEvent() {
        // Required empty public constructor
    }

    public static CreateEvent newInstance(String param1, String param2) {
        CreateEvent fragment = new CreateEvent();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fromFragment = getArguments().getString(ARG_PARAM1);
            employeeId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
        appointmentList = new ArrayList<Appointment>();
        appointmentRepository = new AppointmentRepository();
        userRepository = new UserRepository();
        companyRepository = new CompanyRepository();
        appointmentRepository.getEmployeeAppointments(employeeId).thenAccept(appointments1 -> {
            appointmentList = new ArrayList<>(appointments1);
            userRepository.getByUID(employeeId).thenAccept(user -> {
                if (user != null) {
                    employee = user;
                    companyRepository.getByUID(employee.getCompanyId()).thenAccept(company1 -> {
                        if (company1 != null) {
                            company = company1;
                        }
                    });
                }
            });

        });

        Button createBtn = view.findViewById(R.id.createNewEvent);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = ((EditText) view.findViewById(R.id.appointment_name)).getText().toString();

                if (validateInput(name, view))
                    return;

                if (isAppointmentOverlapping(view, fromTime, toTime, date))
                    return;
                if (isInWorkingPeriod(view, fromTime, toTime, date))
                    return;

                Appointment appointment = new Appointment(UUIDUtil.generateUUID(), name, date, fromTime, toTime, AppointmentStatus.OCCUPIED, employeeId);
                appointmentRepository.create(appointment).thenAccept(appointmentAddedSuccessful -> {
                    if (appointmentAddedSuccessful) {
                        //add notification that needs to be send when appointment is created
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        if (fromFragment.equals("employeeProfile")) {
                            EventsSchedule eventsScheduleFragment = (EventsSchedule) fragmentManager.findFragmentById(R.id.downView); // Assuming fragmentFrame is the container for EventsSchedule
                            if (eventsScheduleFragment != null) {
                                eventsScheduleFragment.refreshAppointments(); // Call a method to refresh appointments
                            }
                            fragmentManager.popBackStack(String.valueOf(R.id.employeeProfile), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                    } else {
                        Toast.makeText(view.getContext(), "Failed to add appointment.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Button cancelBtn = view.findViewById(R.id.cancelNewEventCreation);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fromFragment.equals("employeeProfile"))
                    fragmentManager.popBackStack(String.valueOf(R.id.employeeProfile), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                else
                    fragmentManager.popBackStack(String.valueOf(R.id.fragmentFrame), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        DatePicker startDatePicker = view.findViewById(R.id.startDatePicker);
        date = new Date(startDatePicker.getYear(), startDatePicker.getMonth() + 1, startDatePicker.getDayOfMonth());
        startDatePicker.init(startDatePicker.getYear(), startDatePicker.getMonth(), startDatePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date = new Date(year, monthOfYear + 1, dayOfMonth);
            }
        });
        TimePicker to = view.findViewById(R.id.totp);
        to.setIs24HourView(true);
        to.setCurrentHour(12);
        to.setCurrentMinute(12);
        toTime = new Time(12, 12);
        TimePicker from = view.findViewById(R.id.fromtp);
        from.setIs24HourView(true);
        from.setCurrentHour(12);
        from.setCurrentMinute(12);
        fromTime = new Time(12, 12);
        to.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Log.d("vrijeme", Integer.toString(hourOfDay));
                toTime = new Time(hourOfDay, minute);
            }
        });
        from.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                fromTime = new Time(hourOfDay, minute);
            }
        });

        return view;
    }

    private static boolean validateInput(String name, View view) {
        if (name.isEmpty()) {
            Toast.makeText(view.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return true;
        }
        LocalDate compareDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
        if (LocalDate.now().isAfter(compareDate)) {
            Toast.makeText(view.getContext(), "Date should be after today", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (toTime.getHours() < fromTime.getHours()) {
            Toast.makeText(view.getContext(), "From should be before to", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (toTime.getHours() == fromTime.getHours() && toTime.getMinutes() < fromTime.getMinutes()) {
            Toast.makeText(view.getContext(), "From should be before to", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private boolean isAppointmentOverlapping(View view, Time fromTime, Time toTime, Date date) {
        for (Appointment a : appointmentList) {
            if (a.getDate().equals(date)) {
                if ((fromTime.compareTo(a.getFrom()) >= 0 && fromTime.compareTo(a.getTo()) < 0)
                        || (toTime.compareTo(a.getFrom()) > 0 && toTime.compareTo(a.getTo()) <= 0)) {
                    Toast.makeText(view.getContext(), "It is overlapping with other event", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInWorkingPeriod(View view, Time fromTime, Time toTime, Date date) {
        boolean isPeriodChecked = false;
        if (employee.getWorkingSchedules() != null) {
            for (EmployeeWorkingHours e : employee.getWorkingSchedules()) {
                if (e.getFromDate().compareTo(date) <= 0 && e.getToDate().compareTo(date) >= 0) {
                    isPeriodChecked = true;
                    for (WorkingHoursRecord wh : e.getWeeklySchedule()) {
                        if (wh.getDayOfWeek().equals(getDayOfWeek(date))) {
                            if (wh.getFromTime().compareTo(fromTime) > 0 || wh.getToTime().compareTo(toTime) < 0) {
                                Toast.makeText(view.getContext(), "Event is not in working period", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        }
                    }
                }
            }
        }
        if (!isPeriodChecked) {
            //check company working schedule
            for (WorkingHoursRecord wh : company.getWorkingHoursRecords()) {
                if (wh.getDayOfWeek().equals(getDayOfWeek(date))) {
                    if (wh.getFromTime().compareTo(fromTime) > 0 || wh.getToTime().compareTo(toTime) < 0) {
                        Toast.makeText(view.getContext(), "Event is not in working period", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    public DayOfWeek getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, date.getYear());
        calendar.set(Calendar.MONTH, date.getMonth() - 1); // Month in Calendar class is zero-based
        calendar.set(Calendar.DAY_OF_MONTH, date.getDay());

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        DayOfWeek[] daysOfWeek = {DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY};
        return daysOfWeek[dayOfWeek - 1];
    }
}