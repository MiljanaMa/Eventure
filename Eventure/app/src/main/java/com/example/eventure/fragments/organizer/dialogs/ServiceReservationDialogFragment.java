package com.example.eventure.fragments.organizer.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.fragments.common.RegisterOwnerFragment4;
import com.example.eventure.model.Appointment;
import com.example.eventure.model.Event;
import com.example.eventure.model.EventType;
import com.example.eventure.model.Notification;
import com.example.eventure.model.Reservation;
import com.example.eventure.model.Service;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.NotificationStatus;
import com.example.eventure.model.enums.ReservationStatus;
import com.example.eventure.repositories.AppointmentRepository;
import com.example.eventure.repositories.EventRepository;
import com.example.eventure.repositories.EventTypeRepository;
import com.example.eventure.repositories.NotificationRepository;
import com.example.eventure.repositories.ReservationRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.utils.DateRangeUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ServiceReservationDialogFragment extends DialogFragment {

    private View view;
    private Service service;
    private Spinner employeeSpinner;
    private Spinner eventSpinner;
    private User organizer;
    private User employee;
    private Event event;
    private DatePicker datePicker;
    private TimePicker fromTimePicker;
    private TimePicker toTimePicker;
    private Date fromDateTime;
    private Date toDateTime;
    private Date cancellationDateTime;
    private Boolean doesOverlap = false;
    private Boolean submitDataValid = false;
    private Boolean doesReservedSlotExist = false;
    private UserRepository userRepository;
    private ReservationRepository reservationRepository;
    private AppointmentRepository appointmentRepository;
    private EventRepository eventRepository;

    public ServiceReservationDialogFragment() {

    }

    public static ServiceReservationDialogFragment newInstance(Service service) {
        ServiceReservationDialogFragment fragment = new ServiceReservationDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("SERVICE", service);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            service = getArguments().getParcelable("SERVICE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.service_reservation_dialog_fragment, container, false);
        userRepository = new UserRepository();
        reservationRepository = new ReservationRepository();
        appointmentRepository = new AppointmentRepository();
        eventRepository = new EventRepository();
        setUpEmployeeSpinner();
        setUpEventSpinner();

        datePicker = view.findViewById(R.id.date_picker);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.setMinDate(calendar.getTimeInMillis());
        datePicker.init(year, month, dayOfMonth, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, monthOfYear, dayOfMonth);
                getReservedSlots(selectedDate.getTime());
                submitDataValid = false;
            }
        });
        fromTimePicker = view.findViewById(R.id.from_time_picker);
        fromTimePicker.setIs24HourView(true);
        fromTimePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {

            int newHour = hourOfDay + (int)service.getDuration();
            int newMinute = minute;

            if (newHour >= 24) {
                newHour -= 24;
            }
            toTimePicker.setCurrentHour(newHour);
            toTimePicker.setCurrentMinute(newMinute);
            submitDataValid = false;
        });

        toTimePicker = view.findViewById(R.id.to_time_picker);
        toTimePicker.setIs24HourView(true);
        toTimePicker.setEnabled(false);

        Button btnCancel = view.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button checkAvailabilityButton = view.findViewById(R.id.check_button);
        checkAvailabilityButton.setOnClickListener(v-> {
            fromDateTime = getDateTimeFromPicker(datePicker, fromTimePicker);
            toDateTime = getDateTimeFromPicker(datePicker, toTimePicker);
            cancellationDateTime = subtractDaysFromDate(fromDateTime, service.getCancellationDeadlineInDays());
            DateRangeUtil newReservationRange = new DateRangeUtil(fromDateTime, toDateTime);
            userRepository.getByUID(((User)employeeSpinner.getSelectedItem()).getId()).thenAccept(userEmployee -> {
                if(userEmployee!= null) {
                    employee = userEmployee;
                    appointmentRepository.getEmployeeAppointments(employee.getId()).thenAccept(appointments -> {
                        if (appointments != null) {
                            for (Appointment appointment : appointments) {
                                DateRangeUtil appointmentRange = new DateRangeUtil(getFromDateTimeFromAppointment(appointment), getToDateTimeFromAppointment(appointment));
                                if (newReservationRange.doesOverlap(appointmentRange)) {
                                    doesOverlap = true;
                                    break;
                                }
                            }
                        }

                        reservationRepository.getByEmployee(employee.getId()).thenAccept(reservations -> {
                            if(reservations!= null) {
                                for (Reservation reservation : reservations) {
                                    DateRangeUtil reservationRange = new DateRangeUtil(reservation.getFrom(), reservation.getTo());
                                    if (newReservationRange.doesOverlap(reservationRange)) {
                                        doesOverlap = true;
                                        break;
                                    }
                                }
                            }
                            if(doesOverlap){
                                ((TextView) view.findViewById(R.id.availability_text)).setText("Not Available! Sorry, that time slot already has a appointment. Here are some alternatives:");
                                view.findViewById(R.id.availability_text).setVisibility(View.VISIBLE);
                                doesOverlap = false;
                            }else{
                                ((TextView) view.findViewById(R.id.availability_text)).setText("Available! You can proceed with making this reservation");
                                view.findViewById(R.id.availability_text).setVisibility(View.VISIBLE);
                                submitDataValid = true;
                                doesOverlap = false;
                            }
                        });
                    });
                }
            });
        });

        Button btnSubmit = view.findViewById(R.id.submit_button);
        btnSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(submitDataValid){
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        userRepository.getByEmail(currentUser.getEmail()).thenAccept(userOrganizer -> {
                            organizer = userOrganizer;
                            userRepository.getByUID(((User)employeeSpinner.getSelectedItem()).getId()).thenAccept(userEmployee -> {
                                employee = userEmployee;
                                eventRepository.getByUID(((Event)eventSpinner.getSelectedItem()).getId()).thenAccept(eventFromDB -> {
                                    event = eventFromDB;
                                    Reservation reservation = new Reservation("", organizer.getId(), organizer, employee.getId(), employee, service.getId(), service,
                                            fromDateTime, toDateTime, ReservationStatus.NEW, null, cancellationDateTime, false, employee.getCompanyId(), event.getId());
                                    reservationRepository.create(reservation).thenAccept(reservationAdded -> {
                                        if(reservationAdded){
                                            String title = "New Service Reservation";
                                            String message = "You have a new reservation created by " + organizer.getFirstName() + " " + organizer.getLastName() + ". Please review the details.";
                                            NotificationRepository notificationRepository = new NotificationRepository();
                                            Notification notification = new Notification("", title, message, employee.getId(), organizer.getId(), NotificationStatus.UNREAD);
                                            notificationRepository.create(notification).thenAccept(notificationAdded -> {
                                                if(notificationAdded) {
                                                    Toast.makeText(view.getContext(), "Reservation successfully added!", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(view.getContext(), "Failed to send notification.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            dismiss();
                                        }else{
                                            Toast.makeText(view.getContext(), "Reservation is NOT added, something went wrong!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                });
                            });
                        });
                    }
                }else{
                    ((TextView) view.findViewById(R.id.availability_text)).setText("Please press check availability button");
                    view.findViewById(R.id.availability_text).setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }

    private void setUpEmployeeSpinner() {
        userRepository.getByIds(service.getServiceProviders()).thenAccept(usersFromDB -> {
            if (usersFromDB != null) {
                ArrayAdapter<User> userAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, usersFromDB);
                userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                employeeSpinner = view.findViewById(R.id.employee_spinner);
                employeeSpinner.setAdapter(userAdapter);
            }
        });
    }

    private void setUpEventSpinner() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userRepository.getByEmail(currentUser.getEmail()).thenAccept(userOrganizer -> {
                organizer = userOrganizer;
                eventRepository.getByOrganizerId(organizer.getId()).thenAccept(eventsFromDB -> {
                    if (eventsFromDB != null) {
                        ArrayAdapter<Event> eventAdapter = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_spinner_item, eventsFromDB);
                        eventAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        eventSpinner = view.findViewById(R.id.event_spinner);
                        eventSpinner.setAdapter(eventAdapter);
                    }
                });
            });
        }
    }

    private Date getDateTimeFromPicker(DatePicker datePicker, TimePicker timePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);

        return calendar.getTime();
    }

    private Date getFromDateTimeFromAppointment(Appointment appointment) {
        int day = appointment.getDate().getDay();
        int month = appointment.getDate().getMonth();
        int year = appointment.getDate().getYear();

        int hour = appointment.getFrom().getHours();
        int minute = appointment.getFrom().getMinutes();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, day, hour, minute);

        return calendar.getTime();
    }

    private Date getToDateTimeFromAppointment(Appointment appointment) {
        int day = appointment.getDate().getDay();
        int month = appointment.getDate().getMonth();
        int year = appointment.getDate().getYear();

        int hour = appointment.getTo().getHours();
        int minute = appointment.getTo().getMinutes();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, day, hour, minute);

        return calendar.getTime();
    }

    public static Date subtractDaysFromDate(Date date, int daysToSubtract) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract);

        return calendar.getTime();
    }

    private void getReservedSlots(Date selectedDate) {
        fromDateTime = getDateTimeFromPicker(datePicker, fromTimePicker);
        toDateTime = getDateTimeFromPicker(datePicker, toTimePicker);
        cancellationDateTime = subtractDaysFromDate(fromDateTime, service.getCancellationDeadlineInDays());
        List<DateRangeUtil> reservedSlots = new ArrayList<>();
        userRepository.getByUID(((User)employeeSpinner.getSelectedItem()).getId()).thenAccept(userEmployee -> {
            employee = userEmployee;
            appointmentRepository.getByEmployeeAndDate(employee.getId(), fromDateTime).thenAccept(appointments -> {
                Log.w("uslo", "uslo u app repo");
                if (appointments != null && !appointments.isEmpty()) {
                    for (Appointment appointment : appointments) {
                        Log.w("uslo", "uslo u app repo2");
                        DateRangeUtil dateRange = new DateRangeUtil(getFromDateTimeFromAppointment(appointment), getToDateTimeFromAppointment(appointment));
                        reservedSlots.add(dateRange);
                        doesReservedSlotExist = true;
                    }
                }


                reservationRepository.getByEmployee(employee.getId()).thenAccept(reservations -> {
                    Log.w("uslo", "uslo u res repo");
                    if (reservations != null && !reservations.isEmpty()) {
                        for(Reservation reservation: reservations){
                            Date reservationDate = reservation.getFrom();
                            if (isSameDate(reservationDate, selectedDate)) {
                                Log.w("uslo", "ima res");
                                DateRangeUtil dateRange = new DateRangeUtil(reservation.getFrom(), reservation.getTo());
                                reservedSlots.add(dateRange);
                                doesReservedSlotExist = true;
                            }
                        }
                    }

                    if(doesReservedSlotExist){
                        Log.w("true", "Ima slotova");
                        showReservedSlots(reservedSlots);
                        doesReservedSlotExist = false;

                    }else{
                        Log.w("false", "Nema slotova");
                        ((TextView)view.findViewById(R.id.reserved_slots)).setText("No reserved slots for the selected date");
                        doesReservedSlotExist = false;
                    }
                });

            });
        });
    }

    private void showReservedSlots(List<DateRangeUtil> reservedSlots) {
        StringBuilder message = new StringBuilder("Reserved slots for the selected date:\n");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        for (DateRangeUtil slot : reservedSlots) {
            String startDateString = dateFormat.format(slot.getStart());
            String endDateString = dateFormat.format(slot.getEnd());

            message.append("From: ").append(startDateString).append("\nTo: ").append(endDateString).append("\n\n");
        }

        ((TextView)view.findViewById(R.id.reserved_slots)).setText(message.toString());
    }

    private boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
}