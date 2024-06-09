package com.example.eventure.fragments.common;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.activities.HomeActivity;
import com.example.eventure.model.Notification;
import com.example.eventure.model.Reservation;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.NotificationStatus;
import com.example.eventure.model.enums.UserRole;
import com.example.eventure.repositories.AuthRepository;
import com.example.eventure.repositories.NotificationRepository;
import com.example.eventure.repositories.ReservationRepository;
import com.example.eventure.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginFragment extends Fragment {

    Button btnLogin;
    AuthRepository authRepository;
    UserRepository userRepository;
    ReservationRepository reservationRepository;
    NotificationRepository notificationRepository;
    private View view;

    public LoginFragment() {

    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
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
        view = inflater.inflate(R.layout.login_fragment, container, false);
        authRepository = new AuthRepository();
        userRepository = new UserRepository();
        reservationRepository = new ReservationRepository();
        notificationRepository = new NotificationRepository();

        btnLogin = view.findViewById(R.id.login_button);
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String email = ((EditText)view.findViewById(R.id.email_edit)).getText().toString();
                String password = ((EditText)view.findViewById(R.id.password_edit)).getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(view.getContext(), "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(view.getContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                authRepository.login(email, password)
                        .thenAccept(loginSuccessful -> {
                            if (loginSuccessful) {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("email", email);
                                updates.put("password", password);
                                userRepository.updateCurrentUser(updates).thenAccept(currentUser -> {
                                    navigateUserBasedOnRole(view, email);
                                });
                            } else {
                                Toast.makeText(view.getContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        Button btnRegisterOrganizer = view.findViewById(R.id.register_organizer_button);
        btnRegisterOrganizer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_loginFragment_to_registerOrganizerFragment);
            }
        });

        Button btnRegisterOwner = view.findViewById(R.id.register_owner_button);
        btnRegisterOwner.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_loginFragment_to_registerOwnerFragment);

            }
        });

        return view;
    }

    private void navigateUserBasedOnRole(View view, String email) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userRepository.getByUID(currentUser.getUid())
                    .thenAccept(user -> {
                        if (user != null) {

                            if(user.getRole().equals(UserRole.ADMIN)){
                                Log.w("ADMIN", "Role Admin");
                                NavHostFragment.findNavController(LoginFragment.this)
                                        .navigate(R.id.action_loginFragment_to_category_managment_fragment);
                                Toast.makeText(view.getContext(), "Login successfull", Toast.LENGTH_SHORT).show();
                                fetchUnreadNotifications(currentUser.getUid());
                            }else if(user.getRole().equals(UserRole.OWNER)){
                               // if(currentUser.isEmailVerified()){
                                    Log.w("OWNER", "Role Owner");
                                    NavHostFragment.findNavController(LoginFragment.this)
                                            .navigate(R.id.action_loginFragment_to_owner_main_fragment);
                                    Toast.makeText(view.getContext(), "Login successfull", Toast.LENGTH_SHORT).show();
                                    fetchUnreadNotifications(currentUser.getUid());
                               // }else{
                                //    Toast.makeText(view.getContext(), "Your email is not verified", Toast.LENGTH_SHORT).show();
                               // }
                            }else if(user.getRole().equals(UserRole.EMPLOYEE)){
                               // if(currentUser.isEmailVerified()){
                                    Log.w("EMPLOYEE", "Role Employee");
                                    NavHostFragment.findNavController(LoginFragment.this)
                                            .navigate(R.id.action_loginFragment_to_employee_main_fragment);
                                    Toast.makeText(view.getContext(), "Login successfull", Toast.LENGTH_SHORT).show();
                                    fetchUnreadNotifications(currentUser.getUid());
                                //}else{
                                //    Toast.makeText(view.getContext(), "Your email is not verified", Toast.LENGTH_SHORT).show();
                               // }
                            }else if(user.getRole().equals(UserRole.ORGANIZER)){
                                //if(currentUser.isEmailVerified()) {
                                checkReservationNotifications(user.getId());
                                    Log.w("ORGANIZER", "Role Organizer");
                                    NavHostFragment.findNavController(LoginFragment.this)
                                            .navigate(R.id.action_loginFragment_to_organizer_main_fragment);
                                    Toast.makeText(view.getContext(), "Login successfull", Toast.LENGTH_SHORT).show();
                                fetchUnreadNotifications(currentUser.getUid());
                                //}else{
                                //    Toast.makeText(view.getContext(), "Your email is not verified", Toast.LENGTH_SHORT).show();
                                //}
                            }
                            ((HomeActivity) requireActivity()).updateMenuVisibility();

                        } else {
                            //radimo cekovanje za deaktivaciju
                            userRepository.getByEmail(email)
                                    .thenAccept(user1 -> {
                                        if(user1 == null){
                                            Log.d("User not found", "No such document");
                                        }else{
                                            //deaktiviran a verifikovan, update
                                            Map<String, Object> updates = new HashMap<>();
                                            updates.put("id", currentUser.getUid());
                                            userRepository.updateEmployee(user1.getId(), updates)
                                                    .thenAccept(updatedUser -> {
                                                        Log.d("User updated", "ACTIVE");
                                                        navigate(updatedUser);
                                                    });
                                        }
                                    });
                        }
                    });
        } else {
            Toast.makeText(view.getContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            Log.d("No current user", "No current user");
        }
    }
    private void checkReservationNotifications(String organizerId){
        reservationRepository.getOrganizerRealized(organizerId).thenAccept(reservations -> {
           for(Reservation r: reservations){
               Reservation res = new Reservation(r);
               Notification not = new Notification(UUID.randomUUID().toString(), "Rate company", "You are now allowed to rate company",
               organizerId, r.getEmployeeId(), NotificationStatus.UNREAD);
               notificationRepository.create(not).thenAccept(created ->{
                   res.setNotified(true);
                    reservationRepository.update(res).thenAccept(updated ->{

                    });
               });
           }
        });
    }
    private void navigate(User user){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(user.getRole().equals(UserRole.ADMIN)){
            Log.w("ADMIN", "Role Admin");
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_loginFragment_to_category_managment_fragment);
        }else if(user.getRole().equals(UserRole.OWNER)){
            Log.w("OWNER", "Role Owner");
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_loginFragment_to_owner_main_fragment);
        }else if(user.getRole().equals(UserRole.EMPLOYEE)){
           // if(currentUser.isEmailVerified()){
                Log.w("EMPLOYEE", "Role Employee");
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_loginFragment_to_employee_main_fragment);
                Toast.makeText(view.getContext(), "Login successfull", Toast.LENGTH_SHORT).show();
           // }else{
           //     Toast.makeText(view.getContext(), "Your email is not verified", Toast.LENGTH_SHORT).show();
           // }
        }else if(user.getRole().equals(UserRole.ORGANIZER)){
            Log.w("ORGANIZER", "Role Organizer");
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_loginFragment_to_organizer_main_fragment);
        }
        ((HomeActivity) requireActivity()).updateMenuVisibility();
    }

    private void fetchUnreadNotifications(String userId) {
        NotificationRepository notificationRepository = new NotificationRepository();
        notificationRepository.getUnreadByReceiverId(userId).thenAccept(unreadNotifications -> {
            for (Notification notification : unreadNotifications) {
                showNotification(notification.getTitle(), notification.getMessage());
            }
        });
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("login_channel", "Login Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(requireContext(), "login_channel")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }

}