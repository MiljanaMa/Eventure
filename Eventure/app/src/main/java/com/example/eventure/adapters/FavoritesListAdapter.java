package com.example.eventure.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
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
import com.example.eventure.model.Product;
import com.example.eventure.model.Service;
import com.example.eventure.repositories.FavoritesRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FavoritesListAdapter extends RecyclerView.Adapter<FavoritesListAdapter.FavoritesViewHolder> {

    public List<Favorites> favorites;
    public FragmentManager fragmentManager;

    public FavoritesListAdapter(List<Favorites> favorites, FragmentManager fragmentManager) {
        this.favorites = favorites;
        this.fragmentManager = fragmentManager;
    }

    public void setFavorites(List<Favorites> favorites){
        this.favorites = favorites;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoritesListAdapter.FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_card, parent, false);
        return new FavoritesListAdapter.FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesListAdapter.FavoritesViewHolder holder, int position) {
        Favorites fav = favorites.get(position);
        holder.name.setText(fav.getName());
        holder.description.setText(fav.getDescription());
        holder.type.setText(fav.getItemType());

        holder.bind(fav, fragmentManager, this); //za prusmeravanje na detalje

    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public static class FavoritesViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        TextView type;
        LinearLayout favoritesCard;
        Button removeFromFavoritesBtn;

        public FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.fav_name);
            description = itemView.findViewById(R.id.fav_description);
            type = itemView.findViewById(R.id.fav_type);
            favoritesCard = itemView.findViewById(R.id.favorites_card);
            removeFromFavoritesBtn = itemView.findViewById(R.id.removeFromFavorites);
        }


        public void bind(Favorites fav, FragmentManager fragmentManager, FavoritesListAdapter adapter) {
            name.setText(fav.getName());
            description.setText(fav.getDescription());
            type.setText(fav.getItemType());

            favoritesCard.setOnClickListener(v -> {
                if (fav.getItemType().equals("product")) {
                    fetchProductAndNavigatee(fav, fragmentManager);
                } else if (fav.getItemType().equals("service")) {
                    fetchServiceAndNavigatee(fav, fragmentManager);
                }
            });

            removeFromFavoritesBtn.setOnClickListener(v -> {
                AlertDialog.Builder dialog = new AlertDialog.Builder(itemView.getContext());
                dialog.setMessage("Are you sure you want to remove this item from favorites?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FavoritesRepository favoritesRepository = new FavoritesRepository();
                        favoritesRepository.delete(fav.getId()).thenAccept(deleted -> {
                            if (deleted) {
                                int position = getAdapterPosition();
                                if (position != RecyclerView.NO_POSITION) {
                                    adapter.favorites.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyItemRangeChanged(position, adapter.favorites.size());
                                    Toast.makeText(itemView.getContext(), "Item removed from favorites.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(itemView.getContext(), "Failed to remove item from favorites.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = dialog.create();
                alert.show();
            });

        }

        private void fetchProductAndNavigatee(Favorites fav, FragmentManager fragmentManager) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("products").document(fav.getItemId()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Product product = documentSnapshot.toObject(Product.class);
                            ProductDetailsFragment fragment = ProductDetailsFragment.newInstance(product);
                            fragmentManager.beginTransaction()
                                    .replace(R.id.nav_favorites, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
        }

        private void fetchServiceAndNavigatee(Favorites fav, FragmentManager fragmentManager) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("services").document(fav.getItemId()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Service service = documentSnapshot.toObject(Service.class);
                            ServiceDetailsFragment fragment = ServiceDetailsFragment.newInstance(service);
                            fragmentManager.beginTransaction()
                                    .replace(R.id.nav_favorites, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
        }

    }
}