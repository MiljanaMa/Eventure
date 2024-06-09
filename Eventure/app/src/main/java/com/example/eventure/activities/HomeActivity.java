package com.example.eventure.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.eventure.R;
import com.example.eventure.databinding.ActivityHomeBinding;
import com.example.eventure.fragments.common.CreateEvent;
import com.example.eventure.model.enums.UserRole;
import com.example.eventure.repositories.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashSet;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Set<Integer> topLevelDestinations = new HashSet<>();
    FirebaseUser currentUser;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("Eventure", "HomeActivity onCreate()");

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        toolbar = binding.activityHomeBase.toolbar;

        updateMenuVisibility();

        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setHomeButtonEnabled(false);
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //topLevelDestinations.add(R.id.nav_language);
        //topLevelDestinations.add(R.id.nav_settings);
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            Log.i("Eventure", "Destination Changed");
            int id = navDestination.getId();
            boolean isTopLevelDestination = topLevelDestinations.contains(id);
        });

         mAppBarConfiguration = new AppBarConfiguration

               .Builder(R.id.nav_login, R.id.nav_register_organizer, R.id.nav_register_owner, R.id.nav_category_managment,  R.id.nav_logout,
                 R.id.owner_main, R.id.nav_notifications)

               .setOpenableLayout(drawer)
                .build();

        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
    }

     @Override
        public boolean onSupportNavigateUp() {
           navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
            return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
       }

    public void updateMenuVisibility() {
        Menu navMenu = navigationView.getMenu();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        setDefaultMenuVisibility(navMenu);

        if(currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            //DocumentReference userInfo = db.collection("users").document(currentUser.getUid());

            db.collection("users").whereEqualTo("id", currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String roleString = document.getString("role");

                            navMenu.findItem(R.id.nav_login).setVisible(false);
                            navMenu.findItem(R.id.nav_register_organizer).setVisible(false);
                            navMenu.findItem(R.id.nav_register_owner).setVisible(false);
                            navMenu.findItem(R.id.nav_offer_search).setVisible(true);
                            navMenu.findItem(R.id.nav_logout).setVisible(true);
                            navMenu.findItem(R.id.nav_category_managment).setVisible(roleString.equals("ADMIN"));
                            navMenu.findItem(R.id.nav_owner_main).setVisible(roleString.equals("OWNER"));
                            navMenu.findItem(R.id.nav_employee_main).setVisible(roleString.equals("EMPLOYEE"));
                            navMenu.findItem(R.id.nav_organizer_main).setVisible(roleString.equals("ORGANIZER"));
                            navMenu.findItem(R.id.nav_event_type_managment).setVisible(roleString.equals("ADMIN"));
                            navMenu.findItem(R.id.next_event_creation1).setVisible(roleString.equals("ORGANIZER"));
                            navMenu.findItem(R.id.nav_employee_products).setVisible(roleString.equals("EMPLOYEE"));
                            navMenu.findItem(R.id.nav_owner_products).setVisible(roleString.equals("OWNER"));
                            navMenu.findItem(R.id.employee_management).setVisible(roleString.equals("OWNER"));
                            navMenu.findItem(R.id.ratings).setVisible(roleString.equals("OWNER"));
                            navMenu.findItem(R.id.nav_subcategory_suggestions_managment).setVisible(roleString.equals("ADMIN"));
                            navMenu.findItem(R.id.nav_employee_services).setVisible(roleString.equals("EMPLOYEE"));
                            navMenu.findItem(R.id.nav_owner_services).setVisible(roleString.equals("OWNER"));
                            navMenu.findItem(R.id.nav_employee_packages).setVisible(roleString.equals("EMPLOYEE"));
                            navMenu.findItem(R.id.nav_owner_packages).setVisible(roleString.equals("OWNER"));
                            navMenu.findItem(R.id.nav_employee_profile).setVisible(roleString.equals("EMPLOYEE"));
                            navMenu.findItem(R.id.nav_event_listing_container).setVisible(roleString.equals("ORGANIZER"));
                            navMenu.findItem(R.id.nav_reservations).setVisible(!roleString.equals("ADMIN"));
                            //necu da se prikaze  
                            navMenu.findItem(R.id.events_calendar).setVisible(roleString.equals("A")); //owner
                            navMenu.findItem(R.id.working_schedule).setVisible(roleString.equals("OWNER"));
                            navMenu.findItem(R.id.nav_reports).setVisible(roleString.equals("ADMIN"));
                            navMenu.findItem(R.id.nav_notifications).setVisible(true);
                            navMenu.findItem(R.id.nav_owner_registration_requests).setVisible(roleString.equals("ADMIN"));
                        }
                    } else {
                        Log.d("Error", "Error getting documents: ", task.getException());
                    }
                }

            });

        }
    }

    private void setDefaultMenuVisibility (Menu navMenu){
        navMenu.findItem(R.id.nav_login).setVisible(true);
        navMenu.findItem(R.id.nav_register_organizer).setVisible(true);
        navMenu.findItem(R.id.nav_register_owner).setVisible(true);
        navMenu.findItem(R.id.nav_category_managment).setVisible(false);
        navMenu.findItem(R.id.nav_owner_main).setVisible(false);
        navMenu.findItem(R.id.nav_employee_main).setVisible(false);
        navMenu.findItem(R.id.nav_organizer_main).setVisible(false);
        navMenu.findItem(R.id.nav_logout).setVisible(false);
        navMenu.findItem(R.id.nav_event_type_managment).setVisible(false);
        navMenu.findItem(R.id.next_event_creation1).setVisible(false);
        navMenu.findItem(R.id.nav_employee_products).setVisible(false);
        navMenu.findItem(R.id.nav_owner_products).setVisible(false);
        navMenu.findItem(R.id.employee_management).setVisible(false);
        navMenu.findItem(R.id.nav_subcategory_suggestions_managment).setVisible(false);
        navMenu.findItem(R.id.nav_offer_search).setVisible(true);
        navMenu.findItem(R.id.nav_employee_services).setVisible(false);
        navMenu.findItem(R.id.nav_owner_services).setVisible(false);
        navMenu.findItem(R.id.nav_employee_packages).setVisible(false);
        navMenu.findItem(R.id.nav_owner_packages).setVisible(false);
        navMenu.findItem(R.id.nav_employee_profile).setVisible(false);
        navMenu.findItem(R.id.events_calendar).setVisible(false);
        navMenu.findItem(R.id.working_schedule).setVisible(false);
        navMenu.findItem(R.id.nav_event_listing_container).setVisible(true);
        navMenu.findItem(R.id.nav_notifications).setVisible(false);
        navMenu.findItem(R.id.nav_owner_registration_requests).setVisible(false);
    }
}