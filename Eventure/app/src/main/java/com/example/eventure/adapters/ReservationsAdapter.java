package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.fragments.common.OrganizerProfile;
import com.example.eventure.model.Appointment;
import com.example.eventure.model.Notification;
import com.example.eventure.model.PackageItem;
import com.example.eventure.model.Reservation;
import com.example.eventure.model.Time;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.AppointmentStatus;
import com.example.eventure.model.enums.ConfirmationType;
import com.example.eventure.model.enums.NotificationStatus;
import com.example.eventure.model.enums.ReservationStatus;
import com.example.eventure.model.enums.UserRole;
import com.example.eventure.repositories.AppointmentRepository;
import com.example.eventure.repositories.BudgetRepository;
import com.example.eventure.repositories.EventRepository;
import com.example.eventure.repositories.NotificationRepository;
import com.example.eventure.repositories.PackageRepository;
import com.example.eventure.repositories.ReservationRepository;
import com.example.eventure.repositories.ServiceRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.utils.UUIDUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ReservationViewHolder> {
    private List<Reservation> reservations;
    private FragmentManager fragmentManager;

    public ReservationsAdapter(List<Reservation> reservations, FragmentManager fragmentManager) {
        this.reservations = reservations;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ReservationsAdapter.ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //fix here
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_card, parent, false);
        return new ReservationsAdapter.ReservationViewHolder(view, fragmentManager);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationsAdapter.ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.bind(reservation, fragmentManager);
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public class ReservationViewHolder extends RecyclerView.ViewHolder {
        private UserRepository userRepository;
        private FirebaseUser currentUser;
        private User user;
        private ServiceRepository serviceRepository;
        private ReservationRepository reservationRepository;
        private NotificationRepository notificationRepository;
        private PackageRepository packageRepository;
        private AppointmentRepository appointmentRepository;
        private EventRepository eventRepository;
        private BudgetRepository budgetRepository;
        private FragmentManager fragmentManager;
        private TextView serviceName;
        private TextView reservationEmployee;
        private TextView reservationOrganizer;
        private TextView reservationCancellationDeadline;
        private TextView reservationAppointment;
        private TextView reservationPeriod;
        private TextView serviceLabel;
        private TextView reservationStatus;
        private Button cancelButton;
        private Button acceptButton;
        private Button rejectButton;
        private LinearLayout reservationCard;
        private LinearLayout employeeLayout;

        public ReservationViewHolder(@NonNull View itemView, FragmentManager fragmentManager1) {
            super(itemView);
            userRepository = new UserRepository();
            serviceRepository = new ServiceRepository();
            reservationRepository = new ReservationRepository();
            notificationRepository = new NotificationRepository();
            packageRepository = new PackageRepository();
            appointmentRepository = new AppointmentRepository();
            eventRepository = new EventRepository();
            budgetRepository = new BudgetRepository();

            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            fragmentManager = fragmentManager1;

            reservationCard = (LinearLayout) itemView.findViewById(R.id.oneReservation);
            employeeLayout = (LinearLayout) itemView.findViewById(R.id.employeeLayout);

            reservationAppointment = itemView.findViewById(R.id.textViewDate);
            reservationPeriod = itemView.findViewById(R.id.textViewPeriod);
            reservationCancellationDeadline = itemView.findViewById(R.id.textViewCanncelationDeadline);
            reservationEmployee = itemView.findViewById(R.id.textViewEmployee);
            reservationOrganizer = itemView.findViewById(R.id.textViewOrganizer);
            reservationStatus = itemView.findViewById(R.id.textViewStatus);
            serviceLabel = itemView.findViewById(R.id.serviceLabel);
            serviceName = itemView.findViewById(R.id.textViewServiceName);
            acceptButton = itemView.findViewById(R.id.acceptReservation);
            cancelButton = itemView.findViewById(R.id.cancelReservation);
            rejectButton = itemView.findViewById(R.id.rejectReservation);
        }

        public void bind(Reservation reservation, FragmentManager fragmentManager) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            reservationAppointment.setText(sdf.format(reservation.getFrom()));

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            reservationPeriod.setText(timeFormat.format(reservation.getFrom()) + " - " + timeFormat.format(reservation.getTo()));

            reservationCancellationDeadline.setText(sdf.format(reservation.getCancellationDeadline()));
            userRepository.getByUID(reservation.getEmployeeId()).thenAccept(employee -> {
                if (employee != null && reservation.getPackageId() == null) {
                    reservationEmployee.setText(employee.getFirstName() + " " + employee.getLastName());
                } else {
                    employeeLayout.setVisibility(View.GONE);
                }
            });
            if (reservation.getPackageId() == null) {
                serviceLabel.setText("Service:");
                setService(reservation, fragmentManager);
            } else {
                serviceLabel.setText("\nPackage services:");
                setPackageServices(reservation, sdf, timeFormat);
            }
            userRepository.getByUID(reservation.getOrganizerId()).thenAccept(organizer -> {
                if (organizer != null) {
                    reservationOrganizer.setText(organizer.getFirstName() + " " + organizer.getLastName());
                    if (user.getRole().equals(UserRole.OWNER)) {
                        reservationCard.setOnClickListener(v -> {
                            OrganizerProfile organizerProfile = OrganizerProfile.newInstance(organizer);
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(R.id.allReservations, organizerProfile);
                            transaction.addToBackStack(String.valueOf(R.id.allReservations));
                            transaction.commit();
                        });
                    }
                }
            });
            reservationStatus.setText(reservation.getStatus().toString());
        }

        private void setPackageServices(Reservation reservation, SimpleDateFormat sdf, SimpleDateFormat timeFormat) {
            StringBuilder packageServicesText = new StringBuilder();
            AtomicInteger counter = new AtomicInteger(0);
            AtomicInteger completedCount = new AtomicInteger(0);
            packageRepository.getByUID(reservation.getPackageId()).thenAccept(packageDb -> {
                if (packageDb != null) {
                    int totalItems = packageDb.getItems().size();
                    for (PackageItem item : packageDb.getItems()) {
                        serviceRepository.getByUID(item.getServiceId()).thenAccept(service -> {
                            if (service != null) {
                                userRepository.getByUID(item.getEmployeeId()).thenAccept(employee -> {
                                    if (employee != null) {
                                        int currentIndex = counter.incrementAndGet();
                                        synchronized (packageServicesText) {
                                            packageServicesText.append(currentIndex).append(". \n").append(service.getName()).append(", ").append(employee.getFirstName()).append(" ").append(employee.getLastName()).append("\n").append(sdf.format(item.getFrom())).append(" from ").append(timeFormat.format(item.getFrom())).append(" to ").append(timeFormat.format(item.getTo())).append("\n");
                                        }
                                    }
                                    checkAllCompleted(totalItems, completedCount, serviceName, packageServicesText);
                                });
                            }
                        });
                    }
                    userRepository.getByUID(currentUser.getUid()).thenAccept(user1 -> {
                        if (user1 != null) {
                            user = user1;
                            if (user.getRole().equals(UserRole.ORGANIZER)) {
                                acceptButton.setVisibility(View.GONE);
                                rejectButton.setVisibility(View.GONE);
                                cancelButton.setVisibility(((reservation.getStatus().equals(ReservationStatus.NEW) || reservation.getStatus().equals(ReservationStatus.ACCEPTED)) && (!reservation.getCancellationDeadline().before(new Date()))) ? View.VISIBLE : View.GONE);
                                cancelButton.setOnClickListener(v -> cancelReservation(reservation));
                            } else {
                                acceptButton.setVisibility((reservation.getStatus().toString().equals(ReservationStatus.NEW.toString()) && packageDb.getManualConfirmation()) ? View.VISIBLE : View.GONE);
                                rejectButton.setVisibility((reservation.getStatus().toString().equals(ReservationStatus.NEW.toString()) && packageDb.getManualConfirmation()) ? View.VISIBLE : View.GONE);

                                acceptButton.setOnClickListener(v -> acceptReservation(reservation));
                                rejectButton.setOnClickListener(v -> rejectReservation(reservation));

                                cancelButton.setVisibility((reservation.getStatus().toString().equals(ReservationStatus.NEW.toString()) ? View.VISIBLE : View.GONE));
                                cancelButton.setOnClickListener(v -> cancelReservation(reservation));
                            }
                        }
                    });
                }
            });
        }

        private void checkAllCompleted(int totalItems, AtomicInteger completedCount, TextView serviceName, StringBuilder packageServicesText) {
            if (completedCount.incrementAndGet() == totalItems) {
                serviceName.setText(packageServicesText.toString());
            }
        }

        private void setService(Reservation reservation, FragmentManager fragmentManager) {
            serviceRepository.getByUID(reservation.getServiceId()).thenAccept(service -> {
                if (service != null) {
                    serviceName.setText(service.getName());

                    userRepository.getByUID(currentUser.getUid()).thenAccept(user1 -> {
                        if (user1 != null) {
                            user = user1;
                            if (user.getRole().equals(UserRole.ORGANIZER)) {
                                acceptButton.setVisibility(View.GONE);
                                rejectButton.setVisibility(View.GONE);
                                cancelButton.setVisibility(((reservation.getStatus().toString().equals(ReservationStatus.NEW.toString()) || reservation.getStatus().toString().equals(ReservationStatus.ACCEPTED.toString())) && (!reservation.getCancellationDeadline().before(new Date()))) ? View.VISIBLE : View.GONE);
                                cancelButton.setOnClickListener(v -> cancelReservation(reservation));
                            } else {
                                //
                                if((reservation.getStatus().toString().equals(ReservationStatus.NEW.toString()) && service.getConfirmationType().equals(ConfirmationType.BYHAND) && (reservation.getPackageId() != null && reservation.getAcceptedEmployeeIds().contains(currentUser.getUid()) && !user.getRole().equals(UserRole.OWNER)))){
                                    acceptButton.setVisibility(View.VISIBLE);
                                }else if((reservation.getStatus().toString().equals(ReservationStatus.NEW.toString()) && service.getConfirmationType().equals(ConfirmationType.BYHAND) && reservation.getPackageId() == null)){
                                    acceptButton.setVisibility(View.VISIBLE);
                                }else{
                                    acceptButton.setVisibility(View.GONE);
                                }
                                //acceptButton.setVisibility((reservation.getStatus().toString().equals(ReservationStatus.NEW.toString()) && service.getConfirmationType().equals(ConfirmationType.BYHAND) && (user.getRole().equals(UserRole.EMPLOYEE) && reservation.getAcceptedEmployeeIds().contains(currentUser.getUid()) && !user.getRole().equals(UserRole.OWNER))) ? View.VISIBLE : View.GONE);
                                rejectButton.setVisibility((reservation.getStatus().toString().equals(ReservationStatus.NEW.toString()) && service.getConfirmationType().equals(ConfirmationType.BYHAND)) ? View.VISIBLE : View.GONE);

                                acceptButton.setOnClickListener(v -> acceptReservation(reservation));
                                rejectButton.setOnClickListener(v -> rejectReservation(reservation));

                                cancelButton.setVisibility((reservation.getStatus().toString().equals(ReservationStatus.NEW.toString()) ? View.VISIBLE : View.GONE));
                                cancelButton.setOnClickListener(v -> cancelReservation(reservation));
                            }
                            userRepository.getByUID(reservation.getOrganizerId()).thenAccept(organizer -> {
                                if (organizer != null) {
                                    reservationOrganizer.setText(organizer.getFirstName() + " " + organizer.getLastName());
                                    if (user.getRole().equals(UserRole.OWNER)) {
                                        reservationCard.setOnClickListener(v -> {
                                            OrganizerProfile organizerProfile = OrganizerProfile.newInstance(organizer);
                                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                                            transaction.replace(R.id.allReservations, organizerProfile);
                                            transaction.addToBackStack(String.valueOf(R.id.allReservations));
                                            transaction.commit();
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }

        private void cancelReservation(Reservation reservation) {
            ReservationStatus oldStatus = reservation.getEnumStatus();
            reservation.setStatus(user.getRole().equals(UserRole.ORGANIZER) ? ReservationStatus.CANCELED_BY_ORGANIZER : ReservationStatus.CANCELED_BY_PUP);
            reservationRepository.update(reservation).thenAccept(updated -> {
                if (updated) {
                    notifyDataSetChanged();
                    //delete appointment if is needed
                    if (oldStatus.equals(ReservationStatus.ACCEPTED)) {
                        for (String appointmentId : reservation.getAppointmentIds()) {
                            appointmentRepository.delete(appointmentId).thenAccept(deleted -> {
                            });
                        }
                    }
                    if (user.getRole().equals(UserRole.ORGANIZER)) {
                        //send notification to employee
                        Notification notification = new Notification(UUIDUtil.generateUUID(), "Reservation", user.getFirstName() + " " + user.getLastName() + " cancelled his reservation", reservation.getEmployeeId(), reservation.getOrganizerId(), NotificationStatus.UNREAD);
                        notificationRepository.create(notification).thenAccept(created -> {
                        });
                    } else {
                        //send notification to organizer
                        Notification notification = new Notification(UUIDUtil.generateUUID(), "Reservation", user.getFirstName() + " " + user.getLastName() + " cancelled reservation", reservation.getOrganizerId(), user.getId(), NotificationStatus.UNREAD);
                        notificationRepository.create(notification).thenAccept(created -> {
                            Notification notificationForRate = new Notification(UUIDUtil.generateUUID(), "Rate", user.getFirstName() + " " + user.getLastName() + "cancelled your reservation, so you can rate company", reservation.getOrganizerId(), user.getId(), NotificationStatus.UNREAD);
                            notificationRepository.create(notificationForRate).thenAccept(created1 -> {
                            });
                        });
                    }
                }
            });
        }

        private void rejectReservation(Reservation reservation) {
            reservation.setStatus(ReservationStatus.REJECTED);
            reservationRepository.update(reservation).thenAccept(reservationUpdated -> {
                if (reservationUpdated) {
                    notifyDataSetChanged();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    Notification not = new Notification(UUID.randomUUID().toString(), "Reservation rejection", "Your reservation from " + (sdf.format(reservation.getFrom()) + " - " + sdf.format(reservation.getTo()) + " is rejected."), reservation.getOrganizerId(), currentUser.getUid(), NotificationStatus.UNREAD);
                    notificationRepository.create(not).thenAccept(send -> {
                    });
                }
            });
        }

        private void acceptReservation(Reservation reservation) {
            if (reservation.getPackageId() == null) {
                reservation.setStatus(ReservationStatus.ACCEPTED);
            } else {
                List<String> updatedList = reservation.getAcceptedEmployeeIds();
                updatedList.remove(currentUser.getUid());
                reservation.setAcceptedEmployeeIds(updatedList);
                if (reservation.getAcceptedEmployeeIds().isEmpty())
                    reservation.setStatus(ReservationStatus.ACCEPTED);

            }
            reservationRepository.update(reservation).thenAccept(updated -> {
                if (updated && reservation.getStatus().toString().equals(ReservationStatus.ACCEPTED.toString())) {
                    notifyDataSetChanged();
                    if (reservation.getPackageId() == null) {
                        com.example.eventure.model.Date date = new com.example.eventure.model.Date(reservation.getFrom().getYear() + 1900, reservation.getFrom().getMonth() + 1, reservation.getFrom().getDate());
                        LocalDateTime localDateTime = reservation.getFrom().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                        int hours = localDateTime.getHour();
                        int minutes = localDateTime.getMinute();
                        Time fromTime = new Time(hours, minutes);
                        LocalDateTime localDateTime1 = reservation.getTo().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        Time toTime = new Time(localDateTime1.getHour(), localDateTime.getMinute());
                        String appointmentId = UUIDUtil.generateUUID();
                        Appointment appointment = new Appointment(appointmentId, "Reservation " + appointmentId, date, fromTime, toTime, AppointmentStatus.RESERVED, reservation.getEmployeeId());

                        appointmentRepository.create(appointment).thenAccept(createdAppointment -> {
                            if (createdAppointment != null) {
                                List<String> appointmentIds = new ArrayList<>();
                                appointmentIds.add(appointmentId);
                                reservation.setAppointmentIds(appointmentIds);
                                reservationRepository.update(reservation).thenAccept(updatedRes -> {
                                });
                            }
                        });
                        //update budget
                        eventRepository.getByUID(reservation.getEventId()).thenAccept(event -> {
                            if (event != null) {
                                budgetRepository.getByUID(event.getBudgetId()).thenAccept(budget -> {
                                    if (budget != null) {
                                        serviceRepository.getByUID(reservation.getServiceId()).thenAccept(service -> {
                                            if (service != null) {
                                                //add money to budget
                                                double money = 0;
                                                money = budget.getMoney() + service.getFullPrice();
                                                budget.setMoney(money);
                                                budgetRepository.update(budget).thenAccept(updatedBudget -> {
                                                });
                                            }

                                        });
                                    }
                                });
                            }
                        });
                    } else {
                        List<String> appointmentIds = new ArrayList<>();

                        // Get the package by UID
                        packageRepository.getByUID(reservation.getPackageId()).thenCompose(packageDb -> {
                            if (packageDb != null) {
                                // Create a list of CompletableFutures for the appointment creation tasks
                                List<CompletableFuture<Void>> futures = new ArrayList<>();

                                for (PackageItem item : packageDb.getItems()) {
                                    // Extract date and time information
                                    com.example.eventure.model.Date date = new com.example.eventure.model.Date(item.getFrom().getYear() + 1900, item.getFrom().getMonth() + 1, item.getFrom().getDate());
                                    LocalDateTime fromDateTime = item.getFrom().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                                    LocalDateTime toDateTime = item.getTo().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                                    // Create Time objects
                                    Time fromTime = new Time(fromDateTime.getHour(), fromDateTime.getMinute());
                                    Time toTime = new Time(toDateTime.getHour(), toDateTime.getMinute());

                                    // Generate appointment ID and create appointment
                                    String appointmentId = UUIDUtil.generateUUID();
                                    Appointment appointment = new Appointment(appointmentId, "Reservation " + appointmentId, date, fromTime, toTime, AppointmentStatus.RESERVED, item.getEmployeeId());

                                    // Add the CompletableFuture for the appointment creation to the list
                                    CompletableFuture<Void> future = appointmentRepository.create(appointment).thenAccept(createdAppointment -> {
                                        if (createdAppointment != null) {
                                            synchronized (appointmentIds) {
                                                appointmentIds.add(appointmentId);
                                            }
                                        }
                                    });

                                    futures.add(future);
                                }
                                //update budget
                                eventRepository.getByUID(reservation.getEventId()).thenAccept(event -> {
                                    if (event != null) {
                                        budgetRepository.getByUID(event.getBudgetId()).thenAccept(budget -> {
                                            if (budget != null) {
                                                //add money to budget
                                                double money = budget.getMoney() + 1457.0;
                                                budget.setMoney(money);
                                                budgetRepository.update(budget).thenAccept(updatedBudget -> {
                                                });
                                            }
                                        });
                                    }
                                });

                                // Wait for all the futures to complete
                                return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                            } else {
                                return CompletableFuture.completedFuture(null);
                            }
                        }).thenCompose(aVoid -> {
                            // Set the appointment IDs to the reservation
                            reservation.setAppointmentIds(appointmentIds);

                            // Update the reservation
                            return reservationRepository.update(reservation);
                        }).thenAccept(updatedRes -> {
                            // Handle the completion of the reservation update if needed
                        }).exceptionally(e -> {
                            // Handle any exceptions that occurred during the process
                            e.printStackTrace();
                            return null;
                        });
                    }
                }
            });
        }
    }
}
