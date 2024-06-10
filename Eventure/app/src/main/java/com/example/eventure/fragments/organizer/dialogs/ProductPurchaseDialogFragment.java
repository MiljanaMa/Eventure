package com.example.eventure.fragments.organizer.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.model.Budget;
import com.example.eventure.model.BudgetItem;
import com.example.eventure.model.Event;
import com.example.eventure.model.Product;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.OfferType;
import com.example.eventure.repositories.BudgetItemRepository;
import com.example.eventure.repositories.BudgetRepository;
import com.example.eventure.repositories.EventRepository;
import com.example.eventure.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ProductPurchaseDialogFragment extends DialogFragment {

    private View view;
    private User organizer;
    private Spinner eventSpinner;
    private UserRepository userRepository;
    private EventRepository eventRepository;
    private Event event;
    private Product product;
    private Budget budget;

    public ProductPurchaseDialogFragment() {
        // Required empty public constructor
    }

    public static ProductPurchaseDialogFragment newInstance(Product product) {
        ProductPurchaseDialogFragment fragment = new ProductPurchaseDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("PRODUCT", product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = getArguments().getParcelable("PRODUCT");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.product_purchase_dialog_fragment, container, false);
         userRepository = new UserRepository();
         eventRepository = new EventRepository();

         setUpEventSpinner();

        Button btnCancel = view.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button btnSubmit = view.findViewById(R.id.submit_button);
        btnSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Event event = ((Event)eventSpinner.getSelectedItem());
                BudgetItem budgetItem = new BudgetItem("", event.getBudgetId(), product.getId(), OfferType.PRODUCT, true, false);
                BudgetItemRepository budgetItemRepository = new BudgetItemRepository();
                budgetItemRepository.create(budgetItem).thenAccept(createdBudgetItemId -> {
                    if(createdBudgetItemId != null){
                        BudgetRepository budgetRepository = new BudgetRepository();
                        Log.w("uslo", "uslo 1");
                        budgetRepository.getByUID(event.getBudgetId()).thenAccept(budgetFromDB -> {
                            if (budgetFromDB != null) {
                                Log.w("uslo", "uslo 2");
                                budget = budgetFromDB;
                                List<String> budgetItemsIds = budget.getBudgetItemsIds();
                                budgetItemsIds.add(budgetItem.getId());
                                budget.setBudgetItemsIds(budgetItemsIds);
                                double money = budget.getMoney() + product.getPrice();
                                budget.setMoney(money);
                                budgetRepository.update(budget).thenAccept(budgetUpdated -> {
                                    Log.w("uslo", "uslo 3");
                                    if (budgetUpdated) {
                                        Toast.makeText(view.getContext(), "Product is added to budget!", Toast.LENGTH_SHORT).show();
                                        dismiss();
                                    } else {
                                        Toast.makeText(view.getContext(), "Failed to add product to budget.", Toast.LENGTH_SHORT).show();
                                    }

                                });
                            }
                        });
                    }
                });
            }
        });

         return view;
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
}