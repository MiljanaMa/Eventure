package com.example.eventure.fragments.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.example.eventure.R;
import com.example.eventure.adapters.OwnerRegistrationRequestListAdapter;
import com.example.eventure.databinding.OwnerRegistrationRequestsFragmentBinding;
import com.example.eventure.model.Category;
import com.example.eventure.model.EventType;
import com.example.eventure.model.OwnerRegistrationRequest;
import com.example.eventure.repositories.CategoryRepository;
import com.example.eventure.repositories.EventTypeRepository;
import com.example.eventure.repositories.OwnerRegistrationRequestRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OwnerRegistrationRequestsFragment extends Fragment {

    private static final List<OwnerRegistrationRequest> allRequests = new ArrayList<>();
    private static final List<OwnerRegistrationRequest> filteredRequests = new ArrayList<>();
    private OwnerRegistrationRequestRepository requestRepository;
    private OwnerRegistrationRequestsFragmentBinding binding;
    private OwnerRegistrationRequestListAdapter requestsListAdapter;
    private Spinner categorySpinner;
    private Spinner eventTypeSpinner;
    private View view;

    public OwnerRegistrationRequestsFragment() {}

    public static OwnerRegistrationRequestsFragment newInstance(String param1, String param2) {
        OwnerRegistrationRequestsFragment fragment = new OwnerRegistrationRequestsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = OwnerRegistrationRequestsFragmentBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        binding.filterButton.setOnClickListener(v -> {
            if (binding.filterContainer.getVisibility() == View.VISIBLE) {
                binding.filterContainer.setVisibility(View.GONE);
            } else {
                binding.filterContainer.setVisibility(View.VISIBLE);
            }
        });

        binding.finalSearchButton.setOnClickListener(v -> applyFilters());
        binding.resetFilterButton.setOnClickListener(v -> resetFilters());
        requestRepository = new OwnerRegistrationRequestRepository();
        loadRequests();
        setUpCategorySpinner();
        setUpEventTypeSpinner();

        return view;
    }

    private void setUpCategorySpinner() {
        CategoryRepository categoryRepository = new CategoryRepository();
        categoryRepository.getAll().thenAccept(categoriesFromDB -> {
            if (categoriesFromDB != null) {
                ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, categoriesFromDB);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner = view.findViewById(R.id.category_spinner);
                categorySpinner.setAdapter(categoryAdapter);
            }
        });
    }

    private void setUpEventTypeSpinner() {
        EventTypeRepository eventTypeRepository = new EventTypeRepository();
        eventTypeRepository.getAll().thenAccept(eventTypesFromDB -> {
            if (eventTypesFromDB != null) {
                ArrayAdapter<EventType> eventTypeAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, eventTypesFromDB);
                eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                eventTypeSpinner = view.findViewById(R.id.event_type_spinner);
                eventTypeSpinner.setAdapter(eventTypeAdapter);
            }
        });
    }

    private void setUpRequests() {
        RecyclerView requestsListView = view.findViewById(R.id.requests_view);
        requestsListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        requestsListAdapter = new OwnerRegistrationRequestListAdapter(filteredRequests, requireActivity().getSupportFragmentManager(), OwnerRegistrationRequestsFragment.this);
        requestsListView.setAdapter(requestsListAdapter);
    }

    public void loadRequests() {
        requestRepository.getAllPending().thenAccept(requestsFromDB -> {
            if (requestsFromDB != null) {
                filteredRequests.clear();
                filteredRequests.addAll(requestsFromDB);
                allRequests.clear();
                allRequests.addAll(requestsFromDB);
                setUpRequests();
            } else {
                Log.e("OwnerRegistrationRequestsFragment", "Failed to retrieve requests from the database");
            }
        });
    }

    private void applyFilters() {


        String companyName = binding.companyNameSearch.getText().toString().trim();
        String ownerFirstName = binding.ownerFirstnameSearch.getText().toString().trim();
        String ownerLastName = binding.ownerLastnameSearch.getText().toString().trim();
        String companyEmail = binding.companyEmailSearch.getText().toString().trim();
        String ownerEmail = binding.ownerEmailSearch.getText().toString().trim();
        Category selectedCategory = (Category) categorySpinner.getSelectedItem();
        EventType selectedEventType = (EventType) eventTypeSpinner.getSelectedItem();
        long fromDate = getDateFromDatePicker(binding.fromDateFilter);
        long toDate = getDateFromDatePicker(binding.toDateFilter);

        List<OwnerRegistrationRequest> newFilteredRequests = new ArrayList<>();

        for (OwnerRegistrationRequest request : allRequests) {
            boolean matches = true;

            if (!companyName.isEmpty() && !request.getCompany().getName().toLowerCase().contains(companyName.toLowerCase())) {
                matches = false;
            }
            if (!ownerFirstName.isEmpty() && !request.getOwner().getFirstName().toLowerCase().contains(ownerFirstName.toLowerCase())) {
                matches = false;
            }
            if (!ownerLastName.isEmpty() && !request.getOwner().getLastName().toLowerCase().contains(ownerLastName.toLowerCase())) {
                matches = false;
            }
            if (!companyEmail.isEmpty() && !request.getCompany().getEmail().toLowerCase().contains(companyEmail.toLowerCase())) {
                matches = false;
            }
            if (!ownerEmail.isEmpty() && !request.getOwner().getEmail().toLowerCase().contains(ownerEmail.toLowerCase())) {
                matches = false;
            }
            if (selectedCategory != null && !request.getCompany().getCategoriesIds().contains(selectedCategory.getId())) {
                matches = false;
            }
            if (selectedEventType != null && !request.getCompany().getEventTypesIds().contains(selectedEventType.getId())) {
                matches = false;
            }
            if (fromDate > 0 && request.getSubmissionDate().getTime() < fromDate) {
                matches = false;
            }
            if (toDate > 0 && request.getSubmissionDate().getTime() > toDate) {
                matches = false;
            }
            if (matches) {
                newFilteredRequests.add(request);
            }
        }
        updateFilteredRequests(newFilteredRequests);
    }
    private void updateFilteredRequests(List<OwnerRegistrationRequest> newFilteredRequests) {
        int oldSize = filteredRequests.size();
        int newSize = newFilteredRequests.size();

        filteredRequests.clear();
        filteredRequests.addAll(newFilteredRequests);

        if (newSize > oldSize) {
            requestsListAdapter.notifyItemRangeInserted(oldSize, newSize - oldSize);
        } else if (newSize < oldSize) {
            requestsListAdapter.notifyItemRangeRemoved(newSize, oldSize - newSize);
        } else {
            requestsListAdapter.notifyItemRangeChanged(0, newSize);
        }
    }

    private long getDateFromDatePicker(DatePicker datePicker) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);
        // Adjust the time to the beginning of the day
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    public void resetFilters() {
        binding.companyNameSearch.setText("");
        binding.ownerFirstnameSearch.setText("");
        binding.ownerLastnameSearch.setText("");
        binding.companyEmailSearch.setText("");
        binding.ownerEmailSearch.setText("");

        if (categorySpinner != null && categorySpinner.getCount() > 0) {
            categorySpinner.setSelection(0);
        }
        if (eventTypeSpinner != null && eventTypeSpinner.getCount() > 0) {
            eventTypeSpinner.setSelection(0);
        }

        resetDatePickers();
        applyFilters();
        loadRequests();
    }

    private void resetDatePickers() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        binding.fromDateFilter.updateDate(year, month, day);
        binding.toDateFilter.updateDate(year, month, day);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}