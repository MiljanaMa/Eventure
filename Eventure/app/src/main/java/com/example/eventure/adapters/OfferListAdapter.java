package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.fragments.common.ProductDetailsFragment;
import com.example.eventure.fragments.common.ServiceDetailsFragment;
import com.example.eventure.model.Favorites;
import com.example.eventure.model.Notification;
import com.example.eventure.model.Offer;
import com.example.eventure.model.Product;
import com.example.eventure.model.Service;
import com.example.eventure.model.enums.NotificationStatus;
import com.example.eventure.repositories.FavoritesRepository;
import com.example.eventure.repositories.NotificationRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.utils.UUIDUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OfferListAdapter extends RecyclerView.Adapter<OfferListAdapter.OfferViewHolder>{

    private List<Offer> offers;
    private FragmentManager fragmentManager;
    public OfferListAdapter(List<Offer> offers, FragmentManager fragmentManager) {
        this.offers = offers;
        this.fragmentManager = fragmentManager;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OfferListAdapter.OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_card, parent, false);
        return new OfferListAdapter.OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferListAdapter.OfferViewHolder holder, int position) {
        Offer offer = offers.get(position);
        holder.offerName.setText(offer.getName());
        holder.offerCategory.setText(offer.getCategoryId());
        holder.offerSubcategory.setText(offer.getSubcategoryId());
        holder.offerPrice.setText(Double.toString(offer.getPrice()));

        holder.bind(offer, fragmentManager); //za prusmeravanje na detalje

    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    public static class OfferViewHolder extends RecyclerView.ViewHolder {
        TextView offerName;
        TextView offerCategory;
        TextView offerSubcategory;
        TextView offerPrice;
        LinearLayout offerCard;
        Button addToFavoritesBtn;
        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            offerName = itemView.findViewById(R.id.offer_name);
            offerCategory = itemView.findViewById(R.id.offer_category);
            offerSubcategory = itemView.findViewById(R.id.offer_subcategory);
            offerPrice = itemView.findViewById(R.id.offer_price);
            offerCard = itemView.findViewById(R.id.offer_card);
            addToFavoritesBtn = itemView.findViewById(R.id.addToFavorites);
        }

        public void bind(Offer offer, FragmentManager fragmentManager) {
            offerName.setText(offer.getName());
            offerCategory.setText(offer.getCategoryId());
            offerSubcategory.setText(offer.getSubcategoryId());
            offerPrice.setText(Double.toString(offer.getPrice()));

            offerCard.setOnClickListener(v -> {
                if (offer.getType().equals("product")) {
                    fetchProductAndNavigate(offer, fragmentManager);
                } else if (offer.getType().equals("service")) {
                    fetchServiceAndNavigate(offer, fragmentManager);
                }
            });

            addToFavoritesBtn.setOnClickListener(v -> {
                if (offer.getType().equals("product")) {
                    addToFavoritesProduct(offer);
                } else if (offer.getType().equals("service")) {
                    addToFavoritesService(offer);
                }
            });
        }

        private void fetchProductAndNavigate(Offer offer, FragmentManager fragmentManager) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("products").document(offer.getOfferId()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Product product = documentSnapshot.toObject(Product.class);
                            ProductDetailsFragment fragment = ProductDetailsFragment.newInstance(product);
                            fragmentManager.beginTransaction()
                                    .replace(R.id.offer_search_fragment_container, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
        }

        private void fetchServiceAndNavigate(Offer offer, FragmentManager fragmentManager) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("services").document(offer.getOfferId()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Service service = documentSnapshot.toObject(Service.class);
                            ServiceDetailsFragment fragment = ServiceDetailsFragment.newInstance(service);
                            fragmentManager.beginTransaction()
                                    .replace(R.id.offer_search_fragment_container, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
        }

        private void addToFavoritesProduct(Offer offer) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("products").document(offer.getOfferId()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Product product = documentSnapshot.toObject(Product.class);
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                Favorites newFavorite = new Favorites();
                                newFavorite.setName(product.getName());
                                newFavorite.setDescription(product.getDescription());
                                newFavorite.setItemId(product.getId());
                                newFavorite.setItemType("product");
                                newFavorite.setOrganizerId(currentUser.getUid());

                                FavoritesRepository favoritesRepository = new FavoritesRepository();
                                NotificationRepository notificationRepository = new NotificationRepository();

                                favoritesRepository.create(newFavorite).thenAccept(createdFavorite -> {
                                    if (createdFavorite != null) {
                                        Toast.makeText(itemView.getContext(), "New product added to favorites list.", Toast.LENGTH_SHORT).show();
                                        Notification notification = new Notification(UUIDUtil.generateUUID(), "New favorite", "New product added to favorites. Check its details and buy it!", currentUser.getUid(), currentUser.getUid(), NotificationStatus.UNREAD);
                                        notificationRepository.create(notification);
                                    } else {
                                        Toast.makeText(itemView.getContext(), "Failed to add new product to favorites.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
        }

        private void addToFavoritesService(Offer offer) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("services").document(offer.getOfferId()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Service service = documentSnapshot.toObject(Service.class);
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                Favorites newFavorite = new Favorites();
                                newFavorite.setName(service.getName());
                                newFavorite.setDescription(service.getDescription());
                                newFavorite.setItemId(service.getId());
                                newFavorite.setItemType("service");
                                newFavorite.setOrganizerId(currentUser.getUid());

                                FavoritesRepository favoritesRepository = new FavoritesRepository();
                                NotificationRepository notificationRepository = new NotificationRepository();

                                favoritesRepository.create(newFavorite).thenAccept(createdFavorite -> {
                                    if (createdFavorite != null) {
                                        Toast.makeText(itemView.getContext(), "New service added to favorites list.", Toast.LENGTH_SHORT).show();
                                        Notification notification = new Notification(UUIDUtil.generateUUID(), "New favorite", "New service added to favorites. Check its details and book it!", currentUser.getUid(), currentUser.getUid(), NotificationStatus.UNREAD);
                                        notificationRepository.create(notification);
                                    } else {
                                        Toast.makeText(itemView.getContext(), "Failed to add new service to favorites.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
        }

    }
}
