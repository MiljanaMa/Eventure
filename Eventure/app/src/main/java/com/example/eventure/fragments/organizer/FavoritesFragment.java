package com.example.eventure.fragments.organizer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventure.R;
import com.example.eventure.adapters.EventListAdapter;
import com.example.eventure.adapters.FavoritesListAdapter;
import com.example.eventure.model.Event;
import com.example.eventure.model.Favorites;
import com.example.eventure.model.User;
import com.example.eventure.repositories.FavoritesRepository;
import com.example.eventure.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {

    private static final List<Favorites> favorites = new ArrayList<>();
    private RecyclerView favoritesListView;
    private static FavoritesRepository favoritesRepository;
    private View view;
    private FavoritesListAdapter favoritesListAdapter;
    private FirebaseUser currentUser;
    private static User user;
    private UserRepository userRepository;


    public FavoritesFragment() {
        // Required empty public constructor
    }


    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        loadFavorites();
        favoritesListView = view.findViewById(R.id.favorites_card);
        favoritesListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        favoritesListAdapter = new FavoritesListAdapter(favorites, requireActivity().getSupportFragmentManager());
        favoritesListAdapter.setFavorites(favorites);
        favoritesListView.setAdapter(favoritesListAdapter);

        return view;
    }

    private void setUpData(List<Favorites> favorites) {
        favoritesListAdapter.notifyDataSetChanged();
    }

    private void loadFavorites(){
        final List<Favorites> loadedFavorites = new ArrayList<>();
        userRepository = new UserRepository();
        favoritesRepository = new FavoritesRepository();
        userRepository.getByUID(currentUser.getUid()).thenAccept(userDb -> {
            if(userDb != null){
                user = userDb;
                Log.d("user id ************", user.getId());
                favoritesRepository.getAllByOrganizerId(user.getId()).thenAccept(favoritesFromDB -> {
                    if (favoritesFromDB != null) {
                        loadedFavorites.addAll(favoritesFromDB);
                        favorites.clear();
                        favorites.addAll(loadedFavorites);
                        setUpData(favorites);
                        favoritesListAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Failed to retrieve favorites", "Failed to retrieve favorites from the database");
                    }
                });
            }
        });
    }

    public void refreshFavorites() {
        loadFavorites();
    }


}