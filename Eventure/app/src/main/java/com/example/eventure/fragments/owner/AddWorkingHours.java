package com.example.eventure.fragments.owner;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.fragments.common.WorkingHours;
import com.example.eventure.model.Date;
import com.example.eventure.model.EmployeeWorkingHours;
import com.example.eventure.model.Time;
import com.example.eventure.model.User;
import com.example.eventure.model.WorkingHoursRecord;
import com.example.eventure.model.enums.DayOfWeek;
import com.example.eventure.repositories.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddWorkingHours#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddWorkingHours extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static WorkingHours workingHours;
    private String fromFragment;
    public static Time monfrom;
    public static Time monto;
    public static Time tuefrom;
    public static Time tueto;
    public static Time wenfrom;
    public static Time wento;
    public static Time thrfrom;
    public static Time thrto;
    public static Time frifrom;
    public static Time frito;
    public static Time satfrom;
    public static Time satto;
    public static Time sunfrom;
    public static Time sunto;
    private UserRepository userRepository;
    private User employee;
    private String employeeId;
    public static Date fromDate;
    public static Date toDate;

    public AddWorkingHours() {
        // Required empty public constructor
    }

    public static AddWorkingHours newInstance(String param1, String param2, WorkingHours workingHoursFrag) {
        AddWorkingHours fragment = new AddWorkingHours();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        workingHours = workingHoursFrag;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_working_hours, container, false);

        userRepository = new UserRepository();
        if (!employeeId.equals("")) {
            userRepository.getByUID(employeeId).thenAccept(user -> {
                if (user != null) {
                    employee = user;
                } else {
                    Log.d("Employee not found", "No such document");
                }
            });
        }

        Button btnSave = view.findViewById(R.id.save_working_schedule);
        if (fromFragment.equals("glupo")) {
            btnSave.setVisibility(View.GONE);
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Map<String, Object> updates = new HashMap<>();
                List<EmployeeWorkingHours> ewh = employee.getWorkingSchedules();

                List<WorkingHoursRecord> records = new ArrayList<WorkingHoursRecord>();
                if(validateDate(view))
                    return;

                if(validateTime(view))
                    return;


                records.add(new WorkingHoursRecord(DayOfWeek.MONDAY, monfrom, monto));
                records.add(new WorkingHoursRecord(DayOfWeek.TUESDAY, tuefrom, tueto));
                records.add(new WorkingHoursRecord(DayOfWeek.WEDNESDAY, wenfrom, wento));
                records.add(new WorkingHoursRecord(DayOfWeek.THURSDAY, thrfrom, thrto));
                records.add(new WorkingHoursRecord(DayOfWeek.FRIDAY, frifrom, frito));
                records.add(new WorkingHoursRecord(DayOfWeek.SATURDAY, satfrom, satto));
                records.add(new WorkingHoursRecord(DayOfWeek.SUNDAY, sunfrom, sunto));

                EmployeeWorkingHours newwh = new EmployeeWorkingHours(fromDate, toDate, records);

                if(validateExistingPeriods(view, newwh))
                    return;

                ewh.add(newwh);
                updates.put("workingSchedules", ewh);
                userRepository.updateEmployee(employeeId, updates).thenAccept(updatedUser -> {
                            System.out.println("User updated successfully: " + updatedUser);
                            if (fromFragment.equals("employeeProfile")) {
                                fragmentManager.popBackStack(String.valueOf(R.id.employeeProfile), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                workingHours.resreshView();
                            } else {
                                fragmentManager.popBackStack(String.valueOf(R.id.working_schedule_main), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                workingHours.resreshView();
                            }
                        })
                        .exceptionally(throwable -> {
                            throwable.printStackTrace();
                            return null;
                        });

                if (fromFragment.equals("employeeProfile"))
                    fragmentManager.popBackStack(String.valueOf(R.id.employeeProfile), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                else
                    fragmentManager.popBackStack(String.valueOf(R.id.working_schedule_main), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        Button btnCancel = view.findViewById(R.id.cancelAddingWorkSchedule);
        if (fromFragment.equals("glupo")) {
            btnCancel.setVisibility(View.GONE);
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fromFragment.equals("employeeProfile"))
                    fragmentManager.popBackStack(String.valueOf(R.id.employeeProfile), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                else
                    fragmentManager.popBackStack(String.valueOf(R.id.working_schedule_main), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        DatePicker startDatePicker = view.findViewById(R.id.startDatePickerAdd);
        fromDate = new Date(startDatePicker.getYear(), startDatePicker.getMonth() + 1, startDatePicker.getDayOfMonth());
        startDatePicker.init(startDatePicker.getYear(), startDatePicker.getMonth(), startDatePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                fromDate = new Date(year, monthOfYear + 1, dayOfMonth);
            }
        });
        DatePicker endDatePicker = view.findViewById(R.id.endDatePickerAdd);
        toDate = new Date(endDatePicker.getYear(), endDatePicker.getMonth() + 1, endDatePicker.getDayOfMonth());
        endDatePicker.init(endDatePicker.getYear(), endDatePicker.getMonth(), endDatePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                toDate = new Date(year, monthOfYear + 1, dayOfMonth);
            }
        });
        //set to 24h format
        TimePicker monStart = view.findViewById(R.id.mondayStartPicker);
        monStart.setIs24HourView(true);
        monStart.setCurrentHour(12);
        monStart.setCurrentMinute(12);
        monfrom = new Time(12, 12);
        monStart.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                monfrom = new Time(hourOfDay, minute);
            }
        });
        TimePicker monEnd = view.findViewById(R.id.mondayEndPicker);
        monEnd.setIs24HourView(true);
        monEnd.setCurrentHour(12);
        monEnd.setCurrentMinute(12);
        monto = new Time(12, 12);
        monEnd.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                monto = new Time(hourOfDay, minute);
            }
        });
        TimePicker tuesdayStartPicker = view.findViewById(R.id.tuesdayStartPicker);
        tuesdayStartPicker.setIs24HourView(true);
        tuesdayStartPicker.setCurrentHour(12);
        tuesdayStartPicker.setCurrentMinute(12);
        tuefrom = new Time(12, 12);
        tuesdayStartPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                tuefrom = new Time(hourOfDay, minute);
            }
        });
        TimePicker tuesdayEndPicker = view.findViewById(R.id.tuesdayEndPicker);
        tuesdayEndPicker.setIs24HourView(true);
        tuesdayEndPicker.setCurrentHour(12);
        tuesdayEndPicker.setCurrentMinute(12);
        tueto = new Time(12, 12);
        tuesdayEndPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                tueto = new Time(hourOfDay, minute);
            }
        });
        TimePicker wednesdayStartPicker = view.findViewById(R.id.wednesdayStartPicker);
        wednesdayStartPicker.setIs24HourView(true);
        wednesdayStartPicker.setCurrentHour(12);
        wednesdayStartPicker.setCurrentMinute(12);
        wenfrom = new Time(12, 12);
        wednesdayStartPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                wenfrom = new Time(hourOfDay, minute);
            }
        });
        TimePicker wednesdayEndPicker = view.findViewById(R.id.wednesdayEndPicker);
        wednesdayEndPicker.setIs24HourView(true);
        wednesdayEndPicker.setCurrentHour(12);
        wednesdayEndPicker.setCurrentMinute(12);
        wento = new Time(12, 12);
        wednesdayEndPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                wento = new Time(hourOfDay, minute);
            }
        });
        TimePicker thursdayStartPicker = view.findViewById(R.id.thursdayStartPicker);
        thursdayStartPicker.setIs24HourView(true);
        thursdayStartPicker.setCurrentHour(12);
        thursdayStartPicker.setCurrentMinute(12);
        thrfrom = new Time(12, 12);
        thursdayStartPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                thrfrom = new Time(hourOfDay, minute);
            }
        });
        TimePicker thursdayEndPicker = view.findViewById(R.id.thursdayEndPicker);
        thursdayEndPicker.setIs24HourView(true);
        thursdayEndPicker.setCurrentHour(12);
        thursdayEndPicker.setCurrentMinute(12);
        thrto = new Time(12, 12);
        thursdayEndPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                thrto = new Time(hourOfDay, minute);
            }
        });
        TimePicker fridayStartPicker = view.findViewById(R.id.fridayStartPicker);
        fridayStartPicker.setIs24HourView(true);
        fridayStartPicker.setCurrentHour(12);
        fridayStartPicker.setCurrentMinute(12);
        frifrom = new Time(12, 12);
        fridayStartPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                frifrom = new Time(hourOfDay, minute);
            }
        });
        TimePicker fridayEndPicker = view.findViewById(R.id.fridayEndPicker);
        fridayEndPicker.setIs24HourView(true);
        fridayEndPicker.setCurrentHour(12);
        fridayEndPicker.setCurrentMinute(12);
        frito = new Time(12, 12);
        fridayEndPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                frito = new Time(hourOfDay, minute);
            }
        });
        TimePicker saturdayStartPicker = view.findViewById(R.id.saturdayStartPicker);
        saturdayStartPicker.setIs24HourView(true);
        saturdayStartPicker.setCurrentHour(12);
        saturdayStartPicker.setCurrentMinute(12);
        satfrom = new Time(12, 12);
        saturdayStartPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                satfrom = new Time(hourOfDay, minute);
            }
        });
        TimePicker saturdayEndPicker = view.findViewById(R.id.saturdayEndPicker);
        saturdayEndPicker.setIs24HourView(true);
        saturdayEndPicker.setCurrentHour(12);
        saturdayEndPicker.setCurrentMinute(12);
        satto = new Time(12, 12);
        saturdayEndPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                satto = new Time(hourOfDay, minute);
            }
        });
        TimePicker sundayStartPicker = view.findViewById(R.id.sundayStartPicker);
        sundayStartPicker.setIs24HourView(true);
        sundayStartPicker.setCurrentHour(12);
        sundayStartPicker.setCurrentMinute(12);
        sunfrom = new Time(12, 12);
        sundayStartPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                sunfrom = new Time(hourOfDay, minute);
            }
        });
        TimePicker sundayEndPicker = view.findViewById(R.id.sundayEndPicker);
        sundayEndPicker.setIs24HourView(true);
        sundayEndPicker.setCurrentHour(12);
        sundayEndPicker.setCurrentMinute(12);
        sunto = new Time(12, 12);
        sundayEndPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                sunto = new Time(hourOfDay, minute);
            }
        });

        return view;
    }
    private static boolean validateTime(View view) {

        if (monfrom.compareTo(monto) >= 0) {
            Toast.makeText(view.getContext(), "Invalid monday time", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (tuefrom.compareTo(tueto) >= 0) {
            Toast.makeText(view.getContext(), "Invalid tuesday time", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (wenfrom.compareTo(wento) >= 0) {
            Toast.makeText(view.getContext(), "Invalid wednesday time", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (thrfrom.compareTo(thrto) >= 0) {
            Toast.makeText(view.getContext(), "Invalid thursday time", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (frifrom.compareTo(frito) >= 0) {
            Toast.makeText(view.getContext(), "Invalid friday time", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (satfrom.compareTo(satto) >= 0) {
            Toast.makeText(view.getContext(), "Invalid saturday time", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (sunfrom.compareTo(sunto) >= 0) {
            Toast.makeText(view.getContext(), "Invalid sunday time", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    private static boolean validateDate(View view) {

        if (fromDate.compareTo(toDate) >= 0) {
            Toast.makeText(view.getContext(), "Invalid date range", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    private boolean validateExistingPeriods(View view, EmployeeWorkingHours employeeWorkingHours) {
        for(EmployeeWorkingHours ewh: employee.getWorkingSchedules()){
            if(ewh.isOverlapping(employeeWorkingHours)){
                Toast.makeText(view.getContext(), "Schedule for this period already exists", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }
}