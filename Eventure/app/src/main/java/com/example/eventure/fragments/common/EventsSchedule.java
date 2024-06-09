package com.example.eventure.fragments.common;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.eventure.R;
import com.example.eventure.adapters.EventAdapter;
import com.example.eventure.model.Appointment;
import com.example.eventure.model.Date;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.AppointmentStatus;
import com.example.eventure.repositories.AppointmentRepository;
import com.example.eventure.settings.FragmentTransition;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventsSchedule#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsSchedule extends Fragment {
    private TextInputEditText weekPickerEditText;
    private Button openWeekPickerButton;
    private RecyclerView recyclerView;
    private List<Appointment> appointments;
    private EventAdapter eventAdapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String fromFragment;
    private String employeeId;
    private AppointmentRepository appointmentRepository;
    private static View mainView;
    private Date fromDate;
    private Date toDate;

    public EventsSchedule() {
        // Required empty public constructor
    }

    public static EventsSchedule newInstance(String param1, String param2) {
        EventsSchedule fragment = new EventsSchedule();
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
        View view = inflater.inflate(R.layout.fragment_events_schedule, container, false);
        appointmentRepository = new AppointmentRepository();
        appointmentRepository.getEmployeeAppointments(employeeId).thenAccept(appointments1 -> {
            //is sort direction good
            Collections.sort(appointments1, new Comparator<Appointment>() {
                @Override
                public int compare(Appointment appointment1, Appointment appointment2) {
                    // Compare by date
                    int dateComparison = appointment1.getDate().compareTo(appointment2.getDate());
                    if (dateComparison != 0) {
                        return dateComparison;
                    }

                    // If dates are equal, compare by from time
                    return appointment1.getFrom().compareTo(appointment2.getFrom());
                }
            });
            appointments = appointments1;
            initializeRecyclerView(view, appointments1);
        });

        Button addBtn = view.findViewById(R.id.addEventBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromFragment != null) {
                    CreateEvent createEventFragment = CreateEvent.newInstance("employeeProfile", employeeId);
                    //createEventFragment.setOnAppointmentAddedListener(this);
                    FragmentTransition.to(createEventFragment, requireActivity(), true, R.id.employeeProfile);
                }else{
                    FragmentTransition.to(CreateEvent.newInstance("", employeeId), requireActivity(), true, R.id.fragmentFrame);
                }
            }
        });
        configureDatePickers(view);
        mainView = view;
        return view;
    }
    private void configureDatePickers(View view){
        weekPickerEditText = view.findViewById(R.id.weekPickerEditText);
        openWeekPickerButton = view.findViewById(R.id.openWeekPickerButton);
        openWeekPickerButton.setOnClickListener(v -> showDatePicker(weekPickerEditText, true, view));
    }
    private void showDatePicker(TextInputEditText datePicker, boolean week, View view1) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                view1.getContext(),
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    if (!week) {
                        datePicker.setText(String.format(Locale.getDefault(), "%02d.%02d.%04d", dayOfMonth1, monthOfYear + 1, year1));
                    } else {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year1, monthOfYear, dayOfMonth1);

                        int selectedDayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK);
                        int firstDayOfWeek = selectedDate.getFirstDayOfWeek();

                        int diff = selectedDayOfWeek - firstDayOfWeek;
                        if (diff < 0) {
                            diff += 7; // Ensure positive value
                        }

                        Calendar firstDayOfWeekCalendar = (Calendar) selectedDate.clone();
                        firstDayOfWeekCalendar.add(Calendar.DAY_OF_MONTH, -diff);

                        Calendar lastDayOfWeekCalendar = (Calendar) firstDayOfWeekCalendar.clone();
                        lastDayOfWeekCalendar.add(Calendar.DAY_OF_MONTH, 6); // Last day of the week

                        String startDate = String.format(Locale.getDefault(), "%02d.%02d.%04d",
                                firstDayOfWeekCalendar.get(Calendar.DAY_OF_MONTH),
                                firstDayOfWeekCalendar.get(Calendar.MONTH) + 1,
                                firstDayOfWeekCalendar.get(Calendar.YEAR));

                        String endDate = String.format(Locale.getDefault(), "%02d.%02d.%04d",
                                lastDayOfWeekCalendar.get(Calendar.DAY_OF_MONTH),
                                lastDayOfWeekCalendar.get(Calendar.MONTH) + 1,
                                lastDayOfWeekCalendar.get(Calendar.YEAR));

                        datePicker.setText(startDate + " - " + endDate);
                        fromDate = new Date(firstDayOfWeekCalendar.get(Calendar.YEAR), firstDayOfWeekCalendar.get(Calendar.MONTH) + 1,
                                firstDayOfWeekCalendar.get(Calendar.DAY_OF_MONTH));
                        toDate = new Date(lastDayOfWeekCalendar.get(Calendar.YEAR), lastDayOfWeekCalendar.get(Calendar.MONTH) + 1,
                                lastDayOfWeekCalendar.get(Calendar.DAY_OF_MONTH));
                        updateAppointmentList();
                    }
                },
                year,
                month,
                dayOfMonth
        );
        datePickerDialog.show();
    }
    private void initializeRecyclerView(View view, List<Appointment> appointments) {
        // Initialize RecyclerView and employee list
        recyclerView = (RecyclerView) view.findViewById(R.id.eventsRecycleViewer);
        eventAdapter = new EventAdapter(appointments);
        recyclerView.setAdapter(eventAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }
    // Inside EventsSchedule fragment
    public void refreshAppointments() {
        // Code to refresh appointments
        appointmentRepository.getEmployeeAppointments(employeeId).thenAccept(appointments -> {
            initializeRecyclerView(mainView, appointments);
        });
    }
    public void updateAppointmentList() {
        List<Appointment> ret = new ArrayList<>();
        for(Appointment a: appointments) {
            if(!fromDate.isAfter(a.getDate()) && toDate.isAfter(a.getDate())){
                ret.add(a);
            }
        }
        Log.d("duzina1", Integer.toString(ret.size()));
        Collections.sort(ret, new Comparator<Appointment>() {
            @Override
            public int compare(Appointment appointment1, Appointment appointment2) {
                // Compare by date
                int dateComparison = appointment1.getDate().compareTo(appointment2.getDate());
                if (dateComparison != 0) {
                    return dateComparison;
                }

                return appointment1.getFrom().compareTo(appointment2.getFrom());
            }
        });
        initializeRecyclerView(mainView, ret);
    }

}