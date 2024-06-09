package com.example.eventure.fragments.common;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.widget.SearchView;

import com.example.eventure.R;
import com.example.eventure.adapters.OfferListAdapter;
import com.example.eventure.model.Offer;
import com.example.eventure.model.enums.OfferType;
import com.example.eventure.repositories.OfferRepository;

import java.util.ArrayList;
import java.util.List;

public class OfferSearchFragment extends Fragment {

    private static final List<Offer> offers = new ArrayList<>();
    private RecyclerView offerListView;
    private OfferListAdapter offerListAdapter;
    private static OfferRepository offerRepository;
    private View view;

    public OfferSearchFragment() {
        // Required empty public constructor
    }

    public static OfferSearchFragment newInstance() {
        OfferSearchFragment fragment = new OfferSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_offer_search, container, false);
        offerRepository = new OfferRepository();
        Button btnFilter = view.findViewById(R.id.filter_button);
        btnFilter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(view.findViewById(R.id.filter_container).getVisibility() == View.VISIBLE){
                    view.findViewById(R.id.filter_container).setVisibility(View.GONE);
                }else{
                    view.findViewById(R.id.filter_container).setVisibility(View.VISIBLE);
                }
            }
        });

        EditText minPriceEditText = view.findViewById(R.id.min_price_filter);
        EditText maxPriceEditText = view.findViewById(R.id.max_price_filter);

        minPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                applyFilters();
            }
        });

        maxPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                applyFilters();
            }
        });

        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterByName(newText);
                return true;
            }
        });

        Spinner typeSpinner = view.findViewById(R.id.event_type_spinner);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        loadOffers();

        return view;
    }

    private void applyFilters() {
        EditText minPriceEditText = view.findViewById(R.id.min_price_filter);
        EditText maxPriceEditText = view.findViewById(R.id.max_price_filter);

        double minPrice;
        double maxPrice;

        try {
            if (minPriceEditText.getText().toString().isEmpty()) {
                minPrice = Double.MIN_VALUE;
            } else {
                minPrice = Double.parseDouble(minPriceEditText.getText().toString());
            }
            if (maxPriceEditText.getText().toString().isEmpty()) {
                maxPrice = Double.MAX_VALUE;
            } else {
                maxPrice = Double.parseDouble(maxPriceEditText.getText().toString());
            }

        } catch (NumberFormatException e) {
            return;
        }

        //Spinner typeSpinner = view.findViewById(R.id.event_type_spinner);
        AppCompatSpinner typeSpinner = view.findViewById(R.id.event_type_spinner);

        String selectedType = typeSpinner.getSelectedItem().toString();

        List<Offer> filteredOffers = new ArrayList<>();
        for (Offer offer : offers) {
            double offerPrice = offer.getPrice();
            String offerType = offer.getType();
            if ((offerPrice >= minPrice && offerPrice <= maxPrice) &&
                    (selectedType.equals("all") || offerType.equalsIgnoreCase(selectedType))) {
                filteredOffers.add(offer);
            }
        }



        offerListAdapter.setOffers(filteredOffers);
    }


    private void setUpProducts(List<Offer> filteredOffers) {
        offerListView = view.findViewById(R.id.offer_view);
        offerListView.setLayoutManager(new LinearLayoutManager(requireContext()));

        offerListAdapter = new OfferListAdapter(filteredOffers, requireActivity().getSupportFragmentManager());
        offerListView.setAdapter(offerListAdapter);
    }

    private void loadOffers(){
        final List<Offer> loadedOffers = new ArrayList<>();
        offerRepository.getAll().thenAccept(offersFromDB -> {
            if (offersFromDB != null) {
                loadedOffers.addAll(offersFromDB);
                offers.clear();
                offers.addAll(loadedOffers);
                setUpProducts(offers);
            } else {
                Log.e("Failed to retrieve offers", "Failed to retrieve offers from the database");
            }
        });
    }

    private void filterByName(String searchText) {
        if (searchText.isEmpty()) {
            if (!offers.isEmpty()) {
                setUpProducts(offers);
            }
            return;
        }
        List<Offer> filteredList = new ArrayList<>();
        for (Offer offer : offers) {
            if (offer.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(offer);
            }
        }
        setUpProducts(filteredList);
    }

}