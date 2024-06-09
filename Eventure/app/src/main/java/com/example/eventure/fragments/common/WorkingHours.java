package com.example.eventure.fragments.common;

import static com.example.eventure.model.enums.DayOfWeek.FRIDAY;
import static com.example.eventure.model.enums.DayOfWeek.MONDAY;
import static com.example.eventure.model.enums.DayOfWeek.SATURDAY;
import static com.example.eventure.model.enums.DayOfWeek.THURSDAY;
import static com.example.eventure.model.enums.DayOfWeek.TUESDAY;
import static com.example.eventure.model.enums.DayOfWeek.WEDNESDAY;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.eventure.R;
import com.example.eventure.fragments.owner.AddWorkingHours;
import com.example.eventure.model.Company;
import com.example.eventure.model.Date;
import com.example.eventure.model.EmployeeWorkingHours;
import com.example.eventure.model.User;
import com.example.eventure.model.WorkingHoursRecord;
import com.example.eventure.repositories.CompanyRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.settings.FragmentTransition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkingHours#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkingHours extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String fromFragment;
    private List<User> employeeList;
    private HashMap<String, String> employeeMap;
    private HashMap<String, Date> periodMap;
    private UserRepository userRepository;
    private CompanyRepository companyRepository;
    private Company company;
    private FirebaseUser currentUser;
    private String companyId;
    private String selectedEmployeeId;
    private String employeeFromProfileId;
    private User employeeFromProfile;
    private String firstEmployeeId;
    private Date firstFromDate;
    private Date selectedFromDate;
    private boolean first;
    private boolean firstPeriod;
    private View mainView;

    public WorkingHours() {
        // Required empty public constructor
    }

    public static WorkingHours newInstance(String param1, String param2) {
        WorkingHours fragment = new WorkingHours();
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
            employeeFromProfileId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_working_hours, container, false);

        first = true;
        firstPeriod = true;
        if (fromFragment == null)
            fromFragment = "";
        if (employeeFromProfileId == null)
            employeeFromProfileId = "";

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRepository = new UserRepository();
        companyRepository = new CompanyRepository();

        employeeList = new ArrayList<User>();
        employeeMap = new HashMap<String, String>();
        periodMap = new HashMap<String, Date>();

        if (fromFragment.equals("employeeProfile")) {
            userRepository.getByUID(employeeFromProfileId).thenAccept(user -> {
                if (user != null) {
                    Spinner spinner2 = view.findViewById(R.id.spinner_chosen_employee);
                    spinner2.setVisibility(View.GONE);
                    employeeFromProfile = user;
                    companyRepository.getByUID(employeeFromProfile.getCompanyId()).thenAccept(company1 -> {
                        company = company1;
                        initializePeriodSpinner(view);
                    });
                }
            });
        } else {
            userRepository.getByUID(currentUser.getUid()).thenAccept(user -> {
                if (user != null) {
                    companyId = user.getCompanyId();
                    Log.d("comp", companyId);
                    //dobavi sve usere
                    userRepository.getCompanyEmployees(companyId).thenAccept(users -> {
                        companyRepository.getByUID(companyId).thenAccept(company1 -> {
                            company = company1;
                            employeeList = users;
                            initializeEmployeeSpinner(view);
                        });
                    });
                } else {
                    Log.d("Owner not found", "No such document");
                }
            });
        }

        Button btnSchedule = view.findViewById(R.id.add_working_schedule);
        if(!currentUser.getEmail().equals("owner@gmail.com")){
            btnSchedule.setVisibility(View.GONE);
        }
        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromFragment.equals("employeeProfile"))
                    FragmentTransition.to(AddWorkingHours.newInstance(fromFragment, employeeFromProfileId, WorkingHours.this), requireActivity(), true, R.id.employeeProfile);
                else
                    FragmentTransition.to(AddWorkingHours.newInstance(fromFragment, selectedEmployeeId, WorkingHours.this), requireActivity(), true, R.id.working_schedule_main);
            }
        });
        mainView = view;
        return view;
    }

    private void initializeEmployeeSpinner(View view) {
        Spinner spinner2 = view.findViewById(R.id.spinner_chosen_employee);

        List<String> options2 = new ArrayList<>();
        for (User employee : employeeList) {
            if (first) {
                firstEmployeeId = employee.getId();
            }
            first = false;
            options2.add(employee.getFirstName() + " " + employee.getLastName());
            employeeMap.put(employee.getFirstName() + " " + employee.getLastName(), employee.getId());
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                options2
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == 0) {
                    TextView textView = (TextView) view;
                    textView.setTypeface(null, Typeface.ITALIC);
                }

                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);

                // Make the first item bold in the dropdown list
                if (position == 0) {
                    TextView textView = (TextView) view;
                    textView.setTypeface(null, Typeface.BOLD);
                }

                return view;
            }
        };
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter1);
        spinner2.setSelection(0, false);
        selectedEmployeeId = firstEmployeeId;
        initializePeriodSpinner(view);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedEmployeeName = (String) parent.getItemAtPosition(position);
                firstPeriod = true;
                selectedEmployeeId = employeeMap.get(selectedEmployeeName);
                initializePeriodSpinner(mainView);
                spinner2.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void initializePeriodSpinner(View view) {
        Spinner spinner = view.findViewById(R.id.spinner_working_period);

        List<String> options = new ArrayList<>();
        if (fromFragment.equals("employeeProfile") && employeeFromProfile.getWorkingSchedules() != null) {
            Collections.sort(employeeFromProfile.getWorkingSchedules(), new Comparator<EmployeeWorkingHours>() {
                @Override
                public int compare(EmployeeWorkingHours o1, EmployeeWorkingHours o2) {
                    return o1.getFromDate().compareTo(o2.getFromDate());
                }
            });
            for (EmployeeWorkingHours ewh : employeeFromProfile.getWorkingSchedules()) {
                if (firstPeriod) {
                    selectedFromDate = ewh.getFromDate();
                }
                firstPeriod = false;
                options.add(ewh.getFromDate().toString() + " - " + ewh.getToDate().toString());
                periodMap.put(ewh.getFromDate().toString() + " - " + ewh.getToDate().toString(), ewh.getFromDate());
            }
        } else {
            for (User employee : employeeList) {
                if (employee.getId().equals(selectedEmployeeId)) {
                    if (employee.getWorkingSchedules() != null) {
                        Collections.sort(employee.getWorkingSchedules(), new Comparator<EmployeeWorkingHours>() {
                            @Override
                            public int compare(EmployeeWorkingHours o1, EmployeeWorkingHours o2) {
                                return o1.getFromDate().compareTo(o2.getFromDate());
                            }
                        });
                        for (EmployeeWorkingHours ewh : employee.getWorkingSchedules()) {
                            if (firstPeriod) {
                                selectedFromDate = ewh.getFromDate();
                            }
                            firstPeriod = false;
                            options.add(ewh.getFromDate().toString() + " - " + ewh.getToDate().toString());
                            periodMap.put(ewh.getFromDate().toString() + " - " + ewh.getToDate().toString(), ewh.getFromDate());
                        }
                        break;
                    }
                    break;
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                options
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == 0) {
                    TextView textView = (TextView) view;
                    textView.setTypeface(null, Typeface.ITALIC);
                }

                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);

                // Make the first item bold in the dropdown list
                if (position == 0) {
                    TextView textView = (TextView) view;
                    textView.setTypeface(null, Typeface.BOLD);
                }

                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, false);
        if (options.isEmpty()) {
            initializeWeeklyScheduleDefault(view);
        } else {
            initializeWeeklySchedule(view);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String date = (String) parent.getItemAtPosition(position);
                selectedFromDate = periodMap.get(date);
                initializeWeeklySchedule(mainView);
                spinner.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

    }

    private void initializeWeeklySchedule(View view) {
        TextView monfrom = view.findViewById(R.id.monfrom);
        TextView monto = view.findViewById(R.id.monto);
        TextView tuefrom = view.findViewById(R.id.tuefrom);
        TextView tueto = view.findViewById(R.id.tueto);
        TextView wenfrom = view.findViewById(R.id.wenfrom);
        TextView wento = view.findViewById(R.id.wento);
        TextView thrfrom = view.findViewById(R.id.thrfrom);
        TextView thrto = view.findViewById(R.id.thrto);
        TextView frifrom = view.findViewById(R.id.frifrom);
        TextView frito = view.findViewById(R.id.frito);
        TextView satfrom = view.findViewById(R.id.satfrom);
        TextView satto = view.findViewById(R.id.satto);
        TextView sunfrom = view.findViewById(R.id.sunfrom);
        TextView sunto = view.findViewById(R.id.sunto);
        if (fromFragment.equals("employeeProfile") && employeeFromProfile.getWorkingSchedules() != null) {
            for (EmployeeWorkingHours ewh : employeeFromProfile.getWorkingSchedules()) {
                if (selectedFromDate.equals(ewh.getFromDate())) {
                    for (WorkingHoursRecord wh : ewh.getWeeklySchedule()) {
                        if (wh.getDayOfWeek() == MONDAY) {
                            monfrom.setText(wh.getFromTime().toString());
                            monto.setText(wh.getToTime().toString());
                        } else if (wh.getDayOfWeek() == TUESDAY) {
                            tuefrom.setText(wh.getFromTime().toString());
                            tueto.setText(wh.getToTime().toString());
                        } else if (wh.getDayOfWeek() == WEDNESDAY) {
                            wenfrom.setText(wh.getFromTime().toString());
                            wento.setText(wh.getToTime().toString());
                        } else if (wh.getDayOfWeek() == THURSDAY) {
                            thrfrom.setText(wh.getFromTime().toString());
                            thrto.setText(wh.getToTime().toString());
                        } else if (wh.getDayOfWeek() == FRIDAY) {
                            frifrom.setText(wh.getFromTime().toString());
                            frito.setText(wh.getToTime().toString());
                        } else if (wh.getDayOfWeek() == SATURDAY) {
                            satfrom.setText(wh.getFromTime().toString());
                            satto.setText(wh.getToTime().toString());
                        } else {
                            sunfrom.setText(wh.getFromTime().toString());
                            sunto.setText(wh.getToTime().toString());
                        }

                    }
                    break;
                }
            }
        } else {
            for (User employee : employeeList) {
                if (employee.getId().equals(selectedEmployeeId) && employee.getWorkingSchedules() != null) {
                    for (EmployeeWorkingHours ewh : employee.getWorkingSchedules()) {
                        if (selectedFromDate.equals(ewh.getFromDate())) {
                            for (WorkingHoursRecord wh : ewh.getWeeklySchedule()) {
                                if (wh.getDayOfWeek() == MONDAY) {
                                    monfrom.setText(wh.getFromTime().toString());
                                    monto.setText(wh.getToTime().toString());
                                } else if (wh.getDayOfWeek() == TUESDAY) {
                                    tuefrom.setText(wh.getFromTime().toString());
                                    tueto.setText(wh.getToTime().toString());
                                } else if (wh.getDayOfWeek() == WEDNESDAY) {
                                    wenfrom.setText(wh.getFromTime().toString());
                                    wento.setText(wh.getToTime().toString());
                                } else if (wh.getDayOfWeek() == THURSDAY) {
                                    thrfrom.setText(wh.getFromTime().toString());
                                    thrto.setText(wh.getToTime().toString());
                                } else if (wh.getDayOfWeek() == FRIDAY) {
                                    frifrom.setText(wh.getFromTime().toString());
                                    frito.setText(wh.getToTime().toString());
                                } else if (wh.getDayOfWeek() == SATURDAY) {
                                    satfrom.setText(wh.getFromTime().toString());
                                    satto.setText(wh.getToTime().toString());
                                } else {
                                    sunfrom.setText(wh.getFromTime().toString());
                                    sunto.setText(wh.getToTime().toString());
                                }

                            }
                            break;
                        }
                    }
                } else if (employee.getId().equals(selectedEmployeeId) && employee.getWorkingSchedules() != null) {
                    //dobavi radni kalendar ove nase kompanije
                }
            }
        }
    }

    private void initializeWeeklyScheduleDefault(View view) {
        if (company == null)
            return;

        TextView monfrom = view.findViewById(R.id.monfrom);
        TextView monto = view.findViewById(R.id.monto);
        TextView tuefrom = view.findViewById(R.id.tuefrom);
        TextView tueto = view.findViewById(R.id.tueto);
        TextView wenfrom = view.findViewById(R.id.wenfrom);
        TextView wento = view.findViewById(R.id.wento);
        TextView thrfrom = view.findViewById(R.id.thrfrom);
        TextView thrto = view.findViewById(R.id.thrto);
        TextView frifrom = view.findViewById(R.id.frifrom);
        TextView frito = view.findViewById(R.id.frito);
        TextView satfrom = view.findViewById(R.id.satfrom);
        TextView satto = view.findViewById(R.id.satto);
        TextView sunfrom = view.findViewById(R.id.sunfrom);
        TextView sunto = view.findViewById(R.id.sunto);

        for (WorkingHoursRecord wh : company.getWorkingHoursRecords()) {
            if (wh.getDayOfWeek() == MONDAY) {
                monfrom.setText(wh.getFromTime().toString());
                monto.setText(wh.getToTime().toString());
            } else if (wh.getDayOfWeek() == TUESDAY) {
                tuefrom.setText(wh.getFromTime().toString());
                tueto.setText(wh.getToTime().toString());
            } else if (wh.getDayOfWeek() == WEDNESDAY) {
                wenfrom.setText(wh.getFromTime().toString());
                wento.setText(wh.getToTime().toString());
            } else if (wh.getDayOfWeek() == THURSDAY) {
                thrfrom.setText(wh.getFromTime().toString());
                thrto.setText(wh.getToTime().toString());
            } else if (wh.getDayOfWeek() == FRIDAY) {
                frifrom.setText(wh.getFromTime().toString());
                frito.setText(wh.getToTime().toString());
            } else if (wh.getDayOfWeek() == SATURDAY) {
                satfrom.setText(wh.getFromTime().toString());
                satto.setText(wh.getToTime().toString());
            } else {
                sunfrom.setText(wh.getFromTime().toString());
                sunto.setText(wh.getToTime().toString());
            }

        }
    }

    public void resreshView() {
        if (fromFragment.equals("employeeProfile")) {
            userRepository.getByUID(employeeFromProfileId).thenAccept(user -> {
                if (user != null)
                    employeeFromProfile = user;
                initializePeriodSpinner(mainView);
            });
        } else {
            userRepository.getCompanyEmployees(companyId).thenAccept(users -> {
                employeeList = users;
                initializePeriodSpinner(mainView);
            });
        }
    }
}
