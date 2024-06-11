package com.example.eventure.fragments.organizer;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.eventure.R;
import com.example.eventure.adapters.MessageAdapter;
import com.example.eventure.databinding.ChatBinding;
import com.example.eventure.model.Message;
import com.example.eventure.model.Notification;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.MessageStatus;
import com.example.eventure.model.enums.NotificationStatus;
import com.example.eventure.repositories.MessageRepository;
import com.example.eventure.repositories.NotificationRepository;
import com.example.eventure.repositories.UserRepository;
import com.example.eventure.utils.UUIDUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class ChatFragment extends Fragment {

    private User recipient = new User();
    private String recipientId;
    private User sender = new User();
    private ArrayList<Message> messages = new ArrayList<>();
    private static FirebaseUser currentUser;
    private View root;
    private ChatBinding binding;
    private static final String ARG_SERVICE = "recipientId";


    public ChatFragment() {
        // Required empty public constructor
    }


    public static ChatFragment newInstance(String recipientId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SERVICE, recipientId);
        fragment.setArguments(args);
        fragment.recipientId = recipientId;
        fragment.recipient.setId(recipientId);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        return fragment;
        //ANASTANOTE: ovaj bi trebao da dobije kao argument id primaoca, odnosno ownera
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipientId = getArguments().getString(ARG_SERVICE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //root = inflater.inflate(R.layout.chat, container, false);
        binding = ChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getSender();
        getRecipient();

        Button back = binding.getRoot().findViewById(R.id.buttonBack);
        //Button back = root.findViewById(R.id.buttonBack);
        back.setOnClickListener(e -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        EditText text = binding.getRoot().findViewById(R.id.content);

        Button send = binding.getRoot().findViewById(R.id.sendButton);
        send.setOnClickListener(e -> {
            MessageRepository repo = new MessageRepository();
            Message m = new Message();
            m.setDate(new Date().toString());
            LocalTime currentTime = LocalTime.now();
            m.setTime(currentTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            m.setContent(text.getText().toString());
            m.setRecipientId(recipient.getId());
            m.setSenderId(sender.getId());
            //TODO: mozda setuj sendera preko CurrentUser
            m.setStatus(MessageStatus.UNREAD);
            text.setText("");

            messages.add(m);

            NotificationRepository notificationRepository = new NotificationRepository();
            Notification newNotification = new Notification();
            Notification notification = new Notification(UUIDUtil.generateUUID(), "New message", "New product added to favorites. Check its details and buy it!", recipient.getId(), currentUser.getUid(), NotificationStatus.UNREAD);
            notificationRepository.create(newNotification);

            Comparator<Message> messageComparator = new Comparator<Message>() {
                @Override
                public int compare(Message m1, Message m2) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

                    try {
                        Date date1 = dateFormat.parse(m1.getDate());
                        Date date2 = dateFormat.parse(m2.getDate());

                        int dateComparison = date1.compareTo(date2);
                        if (dateComparison != 0) {
                            return dateComparison;
                        }

                        Date time1 = timeFormat.parse(m1.getTime());
                        Date time2 = timeFormat.parse(m2.getTime());
                        return time1.compareTo(time2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            };
            Collections.sort(messages, messageComparator);


            MessageAdapter adapter;
            //da poreda poruke u zavisnosti da li je trenutni korisnik primalac ili posiljalac
            if(sender.getId().equals(currentUser.getUid()))
                adapter = new MessageAdapter(getActivity(), messages, sender, recipient);
            else
                adapter = new MessageAdapter(getActivity(), messages, recipient, sender);
            ListView listView = binding.getRoot().findViewById(R.id.messages);

            listView.setAdapter(adapter);
            int lastItemPosition = messages.size() - 1;
            listView.setSelection(lastItemPosition);
            MessageRepository messageRepository = new MessageRepository();
            messageRepository.create(m);
            getMessages();
        });

        return root;

    }

    private void getRecipient()
    {
        UserRepository userRepository = new UserRepository();
        userRepository.getByUID(recipientId).thenAccept(user -> {
            if (user != null) {
                recipient = user;
            } else {
                Log.d("Recipent not found", "No such document");
            }
        });
    }
    private void getSender()
    {
        UserRepository userRepository = new UserRepository();
        userRepository.getByUID(currentUser.getUid()).thenAccept(user -> {
            if (user != null) {
                sender = user;
                getMessages();
            } else {
                Log.d("Sender not found", "No such document");
            }
        });
    }

    private void getMessages()
    {
        MessageRepository messageRepository = new MessageRepository();
        messageRepository.getBy2Users(sender.getId(), recipient.getId())
            .thenAccept(messagesDB -> {
               if(messagesDB != null){
                   messages.clear();
                   messages.addAll(messagesDB);
                   // Define a comparator for comparing messages by date and time
                   Comparator<Message> messageComparator = new Comparator<Message>() {
                       @Override
                       public int compare(Message m1, Message m2) {
                           SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                           SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

                           try {
                               Date date1 = dateFormat.parse(m1.getDate());
                               Date date2 = dateFormat.parse(m2.getDate());

                               int dateComparison = date1.compareTo(date2);
                               if (dateComparison != 0) {
                                   return dateComparison;
                               }

                               Date time1 = timeFormat.parse(m1.getTime());
                               Date time2 = timeFormat.parse(m2.getTime());
                               return time1.compareTo(time2);
                           } catch (ParseException e) {
                               e.printStackTrace();
                               return 0;
                           }
                       }
                   };
                   Collections.sort(messages, messageComparator);

                   ListView listView = root.findViewById(R.id.messages);
                   MessageAdapter adapter;
                   adapter = new MessageAdapter(getActivity(), messages, sender, recipient);
                   listView.setAdapter(adapter);
                   int lastItemPosition = messages.size() - 1;
                   listView.setSelection(lastItemPosition);

               }

            });
    }

    final Handler handler = new Handler();
    private Runnable refreshRunnable;

    @Override
    public void onResume() {
        super.onResume();
        startRefreshing();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRefreshing();
    }

    private void startRefreshing() {
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 5000);
                if(isVisible())
                    getMessages();
            }
        };
        handler.postDelayed(refreshRunnable, 5000);
    }

    private void stopRefreshing() {
        handler.removeCallbacks(refreshRunnable);
    }

}