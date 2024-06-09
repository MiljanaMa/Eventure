package com.example.eventure.fragments.owner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.fragments.common.EventsSchedule;
import com.example.eventure.fragments.common.RegisterOrganizerFragment;
import com.example.eventure.fragments.common.WorkingHours;
import com.example.eventure.model.Date;
import com.example.eventure.model.EmployeeWorkingHours;
import com.example.eventure.model.Image;
import com.example.eventure.model.Time;
import com.example.eventure.model.User;
import com.example.eventure.model.WorkingHoursRecord;
import com.example.eventure.model.enums.DayOfWeek;
import com.example.eventure.model.enums.UserRole;
import com.example.eventure.repositories.AuthRepository;
import com.example.eventure.repositories.ImageRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.settings.FragmentTransition;
import com.example.eventure.utils.ImageUtil;
import com.example.eventure.utils.UUIDUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterEmployee#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterEmployee extends Fragment {
    private static final int GET_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Uri imageUri;
    private UserRepository userRepository;
    private AuthRepository authRepository;
    private ImageRepository imageRepository;
    private FirebaseUser currentUser;
    private String companyId;
    private boolean isWHChecked;
    private static EmployeeManagement employeeManagement;

    public RegisterEmployee() {
        // Required empty public constructor
    }

    public static RegisterEmployee newInstance(EmployeeManagement employeeManagementFrag) {
        RegisterEmployee fragment = new RegisterEmployee();
        employeeManagement = employeeManagementFrag;
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
        View view = inflater.inflate(R.layout.fragment_register_empoloyee, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        imageRepository = new ImageRepository();
        userRepository = new UserRepository();
        authRepository = new AuthRepository();
        userRepository.getByUID(currentUser.getUid()).thenAccept(user -> {
            if (user != null) {
                companyId = user.getCompanyId();
            } else {
                Log.d("Owner not found", "No such document");
            }
        });
        isWHChecked = false;
        Button btnRegister = view.findViewById(R.id.registerEmployee);
        Button cancelBtn = view.findViewById(R.id.cancel);
        Button btnUploadImage = view.findViewById(R.id.upload_employee_img_button);
        imageView = view.findViewById(R.id.imageView);
        AddWorkingHours addWorkingHours = AddWorkingHours.newInstance("glupo", "", null);
        FragmentTransition.to(addWorkingHours, requireActivity(), false, R.id.addWh);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User newUser;
                String email = ((EditText) view.findViewById(R.id.email_edit_e)).getText().toString();
                String password = ((EditText) view.findViewById(R.id.password_edit_e)).getText().toString();
                String passwordCheck = ((EditText) view.findViewById(R.id.password_check_edit_e)).getText().toString();
                String firstName = ((EditText) view.findViewById(R.id.first_name_edit_e)).getText().toString();
                String lastName = ((EditText) view.findViewById(R.id.last_name_edit_e)).getText().toString();
                String phone = ((EditText) view.findViewById(R.id.phone_edit_e)).getText().toString();
                String address = ((EditText) view.findViewById(R.id.address_edit_e)).getText().toString();
                String profileImage = imageUri != null ? ImageUtil.encodeImageToBase64(imageUri, view.getContext()) : "";

                if (validateInput(email, password, passwordCheck, firstName, lastName, phone, address, profileImage, view))
                    return;

                //first check if checkbox is checked
                if(isWHChecked) {
                    //add workingHours
                    Time monfrom = AddWorkingHours.monfrom;
                    Time monto = AddWorkingHours.monto;
                    Time tuefrom = AddWorkingHours.tuefrom;
                    Time tueto = AddWorkingHours.tueto;
                    Time wenfrom = AddWorkingHours.wenfrom;
                    Time wento = AddWorkingHours.wento;
                    Time thrfrom = AddWorkingHours.thrfrom;
                    Time thrto = AddWorkingHours.thrto;
                    Time frifrom = AddWorkingHours.frifrom;
                    Time frito = AddWorkingHours.frito;
                    Time satfrom = AddWorkingHours.satfrom;
                    Time satto = AddWorkingHours.satto;
                    Time sunfrom = AddWorkingHours.sunfrom;
                    Time sunto = AddWorkingHours.sunto;
                    Date fromDate = AddWorkingHours.fromDate;
                    Date toDate = AddWorkingHours.toDate;

                    if (fromDate.compareTo(toDate) >= 0) {
                        Toast.makeText(view.getContext(), "Invalid date range", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (monfrom.compareTo(monto) >= 0) {
                        Toast.makeText(view.getContext(), "Invalid monday time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (tuefrom.compareTo(tueto) >= 0) {
                        Toast.makeText(view.getContext(), "Invalid tuesday time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (wenfrom.compareTo(wento) >= 0) {
                        Toast.makeText(view.getContext(), "Invalid wednesday time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (thrfrom.compareTo(thrto) >= 0) {
                        Toast.makeText(view.getContext(), "Invalid thursday time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (frifrom.compareTo(frito) >= 0) {
                        Toast.makeText(view.getContext(), "Invalid friday time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (satfrom.compareTo(satto) >= 0) {
                        Toast.makeText(view.getContext(), "Invalid saturday time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (sunfrom.compareTo(sunto) >= 0) {
                        Toast.makeText(view.getContext(), "Invalid sunday time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<EmployeeWorkingHours> ewh = new ArrayList<EmployeeWorkingHours>();

                    List<WorkingHoursRecord> records = new ArrayList<WorkingHoursRecord>();
                    records.add(new WorkingHoursRecord(DayOfWeek.MONDAY, monfrom, monto));
                    records.add(new WorkingHoursRecord(DayOfWeek.TUESDAY, tuefrom, tueto));
                    records.add(new WorkingHoursRecord(DayOfWeek.WEDNESDAY, wenfrom, wento));
                    records.add(new WorkingHoursRecord(DayOfWeek.THURSDAY, thrfrom, thrto));
                    records.add(new WorkingHoursRecord(DayOfWeek.FRIDAY, frifrom, frito));
                    records.add(new WorkingHoursRecord(DayOfWeek.SATURDAY, satfrom, satto));
                    records.add(new WorkingHoursRecord(DayOfWeek.SUNDAY, sunfrom, sunto));

                    EmployeeWorkingHours newwh = new EmployeeWorkingHours(fromDate, toDate, records);

                    ewh.add(newwh);
                    newUser = new User(UUIDUtil.generateUUID(), firstName, lastName, email, password, phone, address, profileImage, UserRole.EMPLOYEE, true, companyId, ewh);
                }else{
                    newUser = new User(UUIDUtil.generateUUID(), firstName, lastName, email, password, phone, address, profileImage, UserRole.EMPLOYEE, true, companyId, new ArrayList<EmployeeWorkingHours>());
                }
                createUser(newUser, currentUser, view);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(String.valueOf(R.id.EmployeeManagement), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        //dodati ono za pristup galeriji
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GET_IMAGE_REQUEST);
            }
        });
        CheckBox checkbox = view.findViewById(R.id.checkbox_name_work_time);
        RelativeLayout addWh = (RelativeLayout) view.findViewById(R.id.addWh);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    addWh.setVisibility(View.VISIBLE);
                    isWHChecked = true;

                } else {
                    addWh.setVisibility(View.GONE);
                    isWHChecked = false;
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            }
        }
    }

    private static boolean validateInput(String email, String password, String passwordCheck, String firstName, String lastName, String phone, String address, String profileImage, View view) {
        if (email.isEmpty() || password.isEmpty() || passwordCheck.isEmpty() ||
                firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() ||
                address.isEmpty() || profileImage.isEmpty()) {
            Toast.makeText(view.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(view.getContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (password.length() < 6) {
            Toast.makeText(view.getContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (!password.equals(passwordCheck)) {
            Toast.makeText(view.getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void createUser(User newUser, FirebaseUser currentUser, View view) {
        Image image = new Image("profile_", newUser.getProfileImageId());
        if (!isWHChecked) {
            newUser.setWorkingSchedules(new ArrayList<EmployeeWorkingHours>());
        }
        imageRepository.create(image).thenAccept(imageAdded -> {
            if (imageAdded) {
                newUser.setProfileImageId(image.getId());
                authRepository.registerEmployee(newUser.getEmail(), newUser.getPassword())
                        .thenAccept(registerSuccessful -> {
                            if (registerSuccessful) {
                                FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                                newUser.setId(currUser.getUid());
                                userRepository.create(newUser)
                                        .thenAccept(userAddedSuccessful -> {
                                            if (userAddedSuccessful) {
                                                userRepository.getCurrentUser().thenAccept(currentUser1 -> {
                                                    if (currentUser1 != null) {
                                                        authRepository.login(currentUser1.getEmail(), currentUser1.getPassword()).thenAccept(loginSuccessful -> {
                                                            if (loginSuccessful) {
                                                                employeeManagement.refreshEmployees();
                                                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                                fragmentManager.popBackStack(String.valueOf(R.id.EmployeeManagement), FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                                            }
                                                        });
                                                    }
                                                });

                                            } else {
                                                Toast.makeText(view.getContext(), "Failed to register employee.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(view.getContext(), "User is already in auth", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(view.getContext(), "Failed to add image to database.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}