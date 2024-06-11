package com.example.eventure.fragments.common;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.eventure.R;
import com.example.eventure.fragments.common.dialogs.RatingDialog;
import com.example.eventure.fragments.organizer.dialogs.ReportDialog;
import com.example.eventure.model.Category;
import com.example.eventure.model.Company;
import com.example.eventure.model.EventType;
import com.example.eventure.model.Reservation;
import com.example.eventure.repositories.CategoryRepository;
import com.example.eventure.repositories.CompanyRepository;
import com.example.eventure.repositories.EventTypeRepository;
import com.example.eventure.repositories.ImageRepository;
import com.example.eventure.repositories.ReservationRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.utils.ImageUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CompanyProfile extends Fragment {
    private static final String ARG_COMPANYID = "";
    private String mParam1;
    private Company company;
    private ImageRepository imageRepository;
    private UserRepository userRepository;
    private EventTypeRepository eventTypeRepository;
    private CategoryRepository categoryRepository;
    private CompanyRepository companyRepository;
    private ReservationRepository reservationRepository;
    private Ratings ratingsView;
    private View mainView;
    private FirebaseUser currentUser;

    public CompanyProfile() {
        // Required empty public constructor
    }

    public static CompanyProfile newInstance(String param1) {
        CompanyProfile fragment = new CompanyProfile();
        Bundle args = new Bundle();
        args.putString(ARG_COMPANYID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_COMPANYID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_profile, container, false);
        mainView = view;
        categoryRepository = new CategoryRepository();
        eventTypeRepository = new EventTypeRepository();
        imageRepository = new ImageRepository();
        userRepository = new UserRepository();
        companyRepository = new CompanyRepository();
        reservationRepository = new ReservationRepository();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        companyRepository.getByUID(mParam1).thenAccept(company1 -> {
            if (company1 != null) {
                company = company1;
                bindCompany(view);
                setUtilities(view);
                ratingsView = Ratings.newInstance(company.getId());
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.ratingsView, ratingsView);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }

    private void bindCompany(View view) {
        ImageView imageView = view.findViewById(R.id.imageViewCompany);
        TextView textViewName = view.findViewById(R.id.textViewNameInfoCompany);
        TextView textViewEmail = view.findViewById(R.id.textViewEmailInfoCompany);
        TextView textViewPhone = view.findViewById(R.id.textViewPhoneInfoCompany);
        TextView textViewAddress = view.findViewById(R.id.textViewAddressInfoCompany);
        Button addRating = view.findViewById(R.id.addRating);
        Button sendMessageBtn = view.findViewById(R.id.sendMessage);

        //check if it can leave rating
        addRating.setVisibility(View.GONE);
        reservationRepository.getOrganizerRealizedAndCancelled(currentUser.getUid()).thenAccept(reservations -> {
            if (!reservations.isEmpty()) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, -5);
                Date fiveDaysAgo = calendar.getTime();
                //check date
                for(Reservation r: reservations){
                    if(r.getCancellationDate().after(fiveDaysAgo)){
                        addRating.setVisibility(View.VISIBLE);
                        addRating.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RatingDialog dialogFragment = RatingDialog.newInstance(company.getId(), ratingsView, mainView);
                                dialogFragment.show(requireActivity().getSupportFragmentManager(), "RatingDialog");
                            }
                        });
                        break;
                    }
                }
            }
        });

        Button reportCompany = view.findViewById(R.id.reportCompanyBtn);
        reportCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportDialog dialogFragment1 = ReportDialog.newInstance(company.getId(), "company", ratingsView);
                dialogFragment1.show(requireActivity().getSupportFragmentManager(), "ReportDialog");
            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //posalji id vlasnika/ili email i posaji poruku
                String ownerId = company.getOwnerId();
                //treba mi ovde nei prikaz sada
            }
        });

        if (!company.getImagesIds().isEmpty()) {
            imageRepository.getByUID(company.getImagesIds().get(0)).thenAccept(image -> {
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
        }
        textViewEmail.setText(company.getEmail());
        textViewPhone.setText(company.getPhone());
        textViewAddress.setText(company.getAddress());

        userRepository.getByUID(company.getOwnerId()).thenAccept(user -> {
            if (user != null) {
                textViewName.setText(user.getFirstName() + " " + user.getLastName());
            }
        });
    }

    private void setUtilities(View view) {
        Spinner spinner = view.findViewById(R.id.spinnerTypes);

        eventTypeRepository.getByCompany(company.getEventTypesIds())
                .thenAccept(eventTypes -> {
                    List<String> typeNames = new ArrayList<>();
                    typeNames.add("Event types");
                    for (EventType eventType : eventTypes) {
                        typeNames.add(eventType.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, typeNames) {
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
        Spinner spinner1 = view.findViewById(R.id.spinnerCategory);

        categoryRepository.getByCompany(company.getCategoriesIds())
                .thenAccept(categories -> {
                    List<String> categoryNames = new ArrayList<>();
                    categoryNames.add("Categories");
                    for (Category category : categories) {
                        categoryNames.add(category.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categoryNames) {
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
                    spinner1.setAdapter(adapter);
                    spinner1.setSelection(0, false);
                    spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            spinner1.setSelection(0);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Do nothing
                        }
                    });
                });
    }
}