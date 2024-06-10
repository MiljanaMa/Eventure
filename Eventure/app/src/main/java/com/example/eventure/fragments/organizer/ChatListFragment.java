package com.example.eventure.fragments.organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.eventure.R;
import com.example.eventure.adapters.ChatListAdapter;
import com.example.eventure.databinding.ChatListBinding;
import com.example.eventure.model.Event;
import com.example.eventure.model.Message;
import com.example.eventure.model.User;
import com.example.eventure.repositories.MessageRepository;
import com.example.eventure.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    private ChatListBinding binding;
    private User sender = new User();
    private ArrayList<User> users = new ArrayList<>();
    private FirebaseUser currentUser;

    public ChatListFragment() {
        // Required empty public constructor
    }


    public static ChatListFragment newInstance() {
        ChatListFragment fragment = new ChatListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Log.e("Error regrieving users", "Failed to retrieve users");

        getUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ChatListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getUsers();
        getMessages();
        return root;
    }

    private void getUsers()
    {
        final List<User> loadedUsers = new ArrayList<>();
        UserRepository repo = new UserRepository();
        repo.getAll().thenAccept(usersDB -> {
            if(usersDB != null){
                loadedUsers.addAll(usersDB);
                users.clear();
                users.addAll(loadedUsers);
            }else {
                Log.e("Error regrieving users", "Failed to retrieve users");
            }
        });


        for(User user : users){
            if(user.getEmail().equals(currentUser.getEmail()))
            {
                sender = user;
                break;
            }
        }
    }

    private void getMessages()
    {
        MessageRepository messageRepository = new MessageRepository();
        ArrayList<User> usersToShow = new ArrayList<>();
        messageRepository.getByUser(sender.getId())
            .thenAccept(messagesDB -> {
                if (messagesDB != null) {
                    for(Message mess: messagesDB)
                    {
                        for(User u: users)
                        {
                            if((mess.getRecipientId().equals(u.getId()) || mess.getSenderId().equals(u.getId())) && usersToShow.stream().noneMatch(s -> s.getId().equals(u.getId())))
                            {
                                if(!u.getId().equals(sender.getId()))
                                {
                                    usersToShow.add(u);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    Log.e("No users and messages found", "aaa");

                }
            });

            ListView listView = binding.getRoot().findViewById(R.id.chat_list_id);
            ChatListAdapter adapter = new ChatListAdapter(getActivity(), usersToShow);
            listView.setAdapter(adapter);

    }
}

