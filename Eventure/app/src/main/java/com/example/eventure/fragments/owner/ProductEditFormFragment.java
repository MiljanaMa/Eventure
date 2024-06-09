package com.example.eventure.fragments.owner;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.eventure.R;
import com.example.eventure.adapters.EventTypesCheckboxAdapter;
import com.example.eventure.model.EventType;

import java.util.ArrayList;
import java.util.List;


public class ProductEditFormFragment extends Fragment {

    private static final int GET_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private List<EventType> eventTypes;
    private RecyclerView eventTypesCheckboxView;
    private EventTypesCheckboxAdapter eventTypesCheckboxAdapter;

    public ProductEditFormFragment() {
        // Required empty public constructor
    }


    public static ProductEditFormFragment newInstance() {
        ProductEditFormFragment fragment = new ProductEditFormFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.product_edit_form_fragment, container, false);

        setButtons(view);
        setUpEventTypes(view);

        return view;
    }

    private void setButtons(View view) {
        Button btnAddExistingSubcategory = view.findViewById(R.id.add_existing_subcategory_button);
        btnAddExistingSubcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                view.findViewById(R.id.subcategory_container).setVisibility(View.VISIBLE);
                view.findViewById(R.id.subcategory_suggestion_containter).setVisibility((View.GONE));
            }
        });

        Button btnUploadImage  = view.findViewById(R.id.upload_image_button);
        imageView = view.findViewById(R.id.imageView);

        btnUploadImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GET_IMAGE_REQUEST);
            }
        });

        Button btnCancel = view.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(String.valueOf(R.id.owner_product_details_container), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        Button btnEditProduct = view.findViewById(R.id.product_edit_button);
        btnEditProduct.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(String.valueOf(R.id.owner_product_details_container), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
    }

    private void setUpEventTypes(View view) {
        eventTypesCheckboxView = view.findViewById(R.id.events_types_checkbox_view);
        eventTypesCheckboxView.setLayoutManager(new LinearLayoutManager(requireContext()));

        eventTypes = new ArrayList<>();
        prepareEventTypes(eventTypes);

        eventTypesCheckboxAdapter = new EventTypesCheckboxAdapter(eventTypes);
        eventTypesCheckboxView.setAdapter(eventTypesCheckboxAdapter);
    }

    private void prepareEventTypes(List<EventType> eventTypes){

        eventTypes.add(new EventType("1L", "Event Type 1", "Description 1", null, false));
        eventTypes.add(new EventType("2L", "Event Type 2", "Description 2", null, false));
        eventTypes.add(new EventType("3L", "Event Type 3", "Description 3", null, false));
        eventTypes.add(new EventType("4L", "Event Type 4", "Description 4", null, false));
    }
}