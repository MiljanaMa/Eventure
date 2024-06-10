package com.example.eventure.fragments.common;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.fragments.common.dialogs.RatingDialog;
import com.example.eventure.fragments.organizer.dialogs.ProductPurchaseDialogFragment;
import com.example.eventure.fragments.organizer.dialogs.ServiceReservationDialogFragment;
import com.example.eventure.model.Favorites;
import com.example.eventure.model.Notification;
import com.example.eventure.model.Product;
import com.example.eventure.model.enums.NotificationStatus;
import com.example.eventure.repositories.FavoritesRepository;
import com.example.eventure.repositories.NotificationRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.utils.UUIDUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductDetailsFragment extends Fragment {

    private static final String ARG_PRODUCT = "product";
    private Product product;
    private FirebaseUser currentUser;

    public static ProductDetailsFragment newInstance(Product product) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = getArguments().getParcelable(ARG_PRODUCT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);

        if(product != null){
            TextView productCategory = view.findViewById(R.id.product_category);
            TextView productSubcategory = view.findViewById(R.id.product_subcategory);
            TextView productName = view.findViewById(R.id.product_name);
            TextView productDescription = view.findViewById(R.id.product_description);
            TextView productPrice = view.findViewById(R.id.product_price);
            TextView productDiscount = view.findViewById(R.id.product_discount);
            TextView productPriceWithDiscount = view.findViewById(R.id.product_price_with_discount);
            TextView productAvailable = view.findViewById(R.id.product_available);
            TextView productVisible = view.findViewById(R.id.product_visible);

            productCategory.setText(product.getCategoryId());
            productSubcategory.setText(product.getSubcategoryId());
            productName.setText(product.getName());
            productDescription.setText(product.getDescription());
            productPrice.setText(Double.toString(product.getPrice()));
            productDiscount.setText(Double.toString(product.getDiscount()));
            productPriceWithDiscount.setText(Double.toString(product.getPriceWithDiscount()));
            productAvailable.setText(product.getAvailable() != null && product.getAvailable() ? "Yes" : "No");
            productVisible.setText(product.getVisible() != null && product.getVisible() ? "Yes" : "No");

        }

        Button addToFavoritesBtn = view.findViewById(R.id.addToFavorites);
        addToFavoritesBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Favorites newFavorite = new Favorites();
                newFavorite.setName(product.getName());
                newFavorite.setDescription(product.getDescription());
                newFavorite.setItemId(product.getId());
                newFavorite.setItemType("product");

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
                newFavorite.setOrganizerId(currentUser.getUid());

                FavoritesRepository favoritesRepository = new FavoritesRepository();
                NotificationRepository notificationRepository = new NotificationRepository();

                favoritesRepository.create(newFavorite).thenAccept(createdFavorite -> {
                   if(createdFavorite != null){
                       Toast.makeText(view.getContext(), "New product added to favorites list.", Toast.LENGTH_SHORT).show();
                       Notification notification = new Notification(UUIDUtil.generateUUID(), "New favorite", "New product added to favorites. Check its details and buy it!", currentUser.getUid(), currentUser.getUid(), NotificationStatus.UNREAD);
                       notificationRepository.create(notification);
                   } else {
                     Toast.makeText(view.getContext(), "Failed to add new product to favorites.", Toast.LENGTH_SHORT).show();
                   }
                });
            }
        });

        Button buyButton = view.findViewById(R.id.buy_button);
        buyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ProductPurchaseDialogFragment dialogFragment = ProductPurchaseDialogFragment.newInstance(product);
                dialogFragment.show(getFragmentManager(), "ProductPurchaseDialogFragment");
            }
        });

        return view;
    }
}