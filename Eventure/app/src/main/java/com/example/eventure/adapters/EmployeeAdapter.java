package com.example.eventure.adapters;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventure.R;
import com.example.eventure.fragments.common.EmployeeProfile;
import com.example.eventure.model.CurrentUser;
import com.example.eventure.model.User;
import com.example.eventure.repositories.AuthRepository;
import com.example.eventure.repositories.ImageRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.utils.ImageUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {
    private List<User> employees;
    private FragmentManager fragmentManager;

    public EmployeeAdapter(List<User> employees, FragmentManager fragmentManager) {
        this.employees = employees;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public EmployeeAdapter.EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //fix here
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_card, parent, false);
        return new EmployeeAdapter.EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeAdapter.EmployeeViewHolder holder, int position) {
        User employee = employees.get(position);
        holder.bind(employee, fragmentManager);
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private CurrentUser currentUser;
        private UserRepository userRepository;
        private ImageRepository imageRepository;
        private AuthRepository authRepository;
        private LinearLayout employeeCard;
        private ImageView imageView;
        private TextView textViewName;
        private TextView textViewEmail;
        private TextView textViewPhone;
        private TextView textViewAdress;
        private TextView textViewActivation;
        private Button activationButton;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            userRepository = new UserRepository();
            imageRepository = new ImageRepository();
            authRepository = new AuthRepository();
            userRepository.getCurrentUser().thenAccept(currentUser1 -> {
               if(currentUser1 != null)
                   currentUser = currentUser1;
            });
            employeeCard = (LinearLayout) itemView.findViewById(R.id.oneEmployee);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewEmail = (TextView) itemView.findViewById(R.id.textViewEmail);
            textViewPhone = (TextView) itemView.findViewById(R.id.textViewPhone);
            textViewAdress = (TextView) itemView.findViewById(R.id.textViewAddress);
            textViewActivation = (TextView) itemView.findViewById(R.id.textViewActivation);
            activationButton = (Button) itemView.findViewById(R.id.activationButton);
        }

        public void bind(User employee, FragmentManager fragmentManager) {
            employeeCard.setOnClickListener(v -> {
                EmployeeProfile employeeProfile = EmployeeProfile.newInstance(employee);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.EmployeeManagement, employeeProfile);
                transaction.addToBackStack(String.valueOf(R.id.EmployeeManagement));
                transaction.commit();
            });
            if (employee.getProfileImageId() != null) {
                imageRepository.getByUID(employee.getProfileImageId()).thenAccept(image -> {
                    if (image != null) {
                        Bitmap bitmap = ImageUtil.decodeBase64ToImage(image.getImage());
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                            Log.d("Profile image", "Profile image exists");
                        }
                    } else {
                        Log.d("No image", "No image");
                    }
                });
                /*Bitmap bitmap = ImageUtil.decodeBase64ToImage(employee.getProfileImage());
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }*/
            } else {
                Glide.with(itemView).load(employee.getProfileImageId())
                        .placeholder(R.mipmap.employee)
                        .error(R.mipmap.employee)
                        .into(imageView);
            }
            textViewName.setText(employee.getFirstName() + " " + employee.getLastName());
            textViewEmail.setText(employee.getEmail());
            textViewPhone.setText(employee.getPhone());
            textViewAdress.setText(employee.getAddress());
            if (employee.getActive()) {
                textViewActivation.setText("ACTIVE");
                activationButton.setText("Deactivate");
            } else {
                textViewActivation.setText("DEACTIVE");
                activationButton.setText("Activate");
            }
            activationButton.setOnClickListener(v -> {
                AlertDialog.Builder dialog = new AlertDialog.Builder(itemView.getContext());

                if (activationButton.getText().equals("Activate")) {
                    dialog.setMessage("Are you sure you want to activate this account?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("active", true);
                            authRepository.activate(employee.getEmail(), employee.getPassword()).thenAccept(result -> {
                               authRepository.login(currentUser.getEmail(), currentUser.getEmail()).thenAccept(logged ->{
                                   userRepository.updateEmployee(employee.getId(), updates)
                                           .thenAccept(updatedUser -> {
                                               // Handle the updated user object
                                               System.out.println("User updated successfully: " + updatedUser);
                                               employee.setActive(true);
                                               activationButton.setText("Deactivate");
                                               textViewActivation.setText("ACTIVE");
                                               dialog.dismiss();
                                           })
                                           .exceptionally(throwable -> {
                                               throwable.printStackTrace();
                                               return null;
                                           });
                               }) ;
                            });
                            activationButton.setText("Deactivate");
                            textViewActivation.setText("ACTIVE");
                            dialog.dismiss();

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = dialog.create();
                    alert.show();
                } else {
                    dialog.setMessage("Are you sure you want to deactivate this account?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("active", false);
                            authRepository.deactivate(employee.getEmail(), employee.getPassword()).thenAccept(deactivated ->{
                                if(deactivated){
                                    authRepository.login(currentUser.getEmail(), currentUser.getPassword()).thenAccept(logged ->{
                                        if(logged){
                                            userRepository.updateEmployee(employee.getId(), updates)
                                                    .thenAccept(updatedUser -> {
                                                        System.out.println("User updated successfully: " + updatedUser);
                                                        employee.setActive(false);
                                                        activationButton.setText("Activate");
                                                        textViewActivation.setText("DEACTIVE");
                                                        dialog.dismiss();
                                                    })
                                                    .exceptionally(throwable -> {
                                                        throwable.printStackTrace();
                                                        return null;
                                                    });
                                        }
                                    });
                                    //dialog.dismiss();
                                }
                            });

                            activationButton.setText("Activate");
                            textViewActivation.setText("DEACTIVE");
                            dialog.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = dialog.create();
                    alert.show();

                }
            });

        }
    }
}
