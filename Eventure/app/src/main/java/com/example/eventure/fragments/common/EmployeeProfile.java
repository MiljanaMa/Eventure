package com.example.eventure.fragments.common;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eventure.R;
import com.example.eventure.model.Employee;
import com.example.eventure.model.Service;
import com.example.eventure.model.User;
import com.example.eventure.repositories.ImageRepository;
import com.example.eventure.repositories.ServiceRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.settings.FragmentTransition;
import com.example.eventure.utils.ImageUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmployeeProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmployeeProfile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_EMPLOYEE = "employee";
    private ServiceRepository serviceRepository;
    private ImageRepository imageRepository;
    private UserRepository userRepository;
    private FirebaseUser currentUser;

    private User employee;

    public EmployeeProfile() {
        // Required empty public constructor
    }

    public static EmployeeProfile newInstance(User employee) {
        EmployeeProfile fragment = new EmployeeProfile();
        Bundle args = new Bundle();
        args.putParcelable(ARG_EMPLOYEE, employee);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            employee = getArguments().getParcelable(ARG_EMPLOYEE);
            // Use the employee object as needed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employee_profile, container, false);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                EventsSchedule eventsScheduleFragment = (EventsSchedule) fragmentManager.findFragmentById(R.id.downView);
                if (eventsScheduleFragment != null) {
                    fragmentManager.popBackStack(); // Remove EventsSchedule fragment from back stack
                    fragmentManager.popBackStack();
                } else {
                    // Handle back press in the fragment if needed
                    // For example, call super.handleOnBackPressed() to allow normal back press behavior
                }
            }
        });
        serviceRepository = new ServiceRepository();
        imageRepository = new ImageRepository();
        userRepository = new UserRepository();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (employee == null) {
            userRepository.getByUID(currentUser.getUid()).thenAccept(user -> {
                if (user != null) {
                    employee = user;
                    bindEmployee(view);
                    setUtilities(view);
                    //FragmentTransition.to(EventsSchedule.newInstance("employeeProfile", employee.getId()), requireActivity(), false, R.id.downView);
                    EventsSchedule eventsScheduleFragment = EventsSchedule.newInstance("employeeProfile", employee.getId());
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.downView, eventsScheduleFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    FragmentTransition.to(WorkingHours.newInstance("employeeProfile", employee.getId()), requireActivity(), false, R.id.employeeSchedule);
                }
            });
        } else {
            bindEmployee(view);
            setUtilities(view);
            //FragmentTransition.to(EventsSchedule.newInstance("employeeProfile", employee.getId()), requireActivity(), false, R.id.downView);
            EventsSchedule eventsScheduleFragment = EventsSchedule.newInstance("employeeProfile", employee.getId());
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.downView, eventsScheduleFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            FragmentTransition.to(WorkingHours.newInstance("employeeProfile", employee.getId()), requireActivity(), false, R.id.employeeSchedule);
        }
        return view;
    }

    private void bindEmployee(View view) {
        ImageView imageView = view.findViewById(R.id.imageViewInfo);
        TextView textViewName = view.findViewById(R.id.textViewNameInfo);
        TextView textViewEmail = view.findViewById(R.id.textViewEmailInfo);
        TextView textViewPhone = view.findViewById(R.id.textViewPhoneInfo);
        TextView textViewAddress = view.findViewById(R.id.textViewAddressInfo);

        if (employee.getProfileImageId() != null) {
            /*Bitmap bitmap = ImageUtil.decodeBase64ToImage(employee.getProfileImage());
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }*/
            imageRepository.getByUID(employee.getProfileImageId()).thenAccept(image -> {
                if (image != null) {
                    Bitmap bitmap = ImageUtil.decodeBase64ToImage(image.getImage());
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        Log.d("Profile image", "Profile image exists");
                    }
                } else {
                    Log.d("No image", "No image");
                }
            });
        } else {
            //set some default image
            Glide.with(this).load(employee.getProfileImageId())
                    .placeholder(R.mipmap.employee)
                    .error(R.mipmap.employee)
                    .into(imageView);
        }
        textViewName.setText(employee.getFirstName() + " " + employee.getLastName());
        textViewEmail.setText(employee.getEmail());
        textViewPhone.setText(employee.getPhone());
        textViewAddress.setText(employee.getAddress());
    }

    private void setUtilities(View view) {
        Spinner spinner = view.findViewById(R.id.spinner);

        serviceRepository.getServicesByProvider(employee.getId())
                .thenAccept(services -> {
                    List<String> serviceNames = new ArrayList<>();
                    serviceNames.add("Utilities");
                    for (Service service : services) {
                        serviceNames.add(service.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, serviceNames) {
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
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            spinner.setSelection(0);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Do nothing
                        }
                    });
                });
    }
}