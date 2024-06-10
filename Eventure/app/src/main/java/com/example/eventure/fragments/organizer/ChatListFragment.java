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
    private static ArrayList<User> users = new ArrayList<>();
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
        Log.e("chat list fragment", "clf 1");

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
                for(User user : users){
                    if(user.getId().equals(currentUser.getUid()))
                    {
                        sender = user;
                        break;
                    }
                }
                Log.e("users count", "users count " + users.size());
                getMessages();

            }else {
                Log.e("Error regrieving users", "Failed to retrieve users");
            }
        });

        Log.e("chat list fragment", "clf 2 get users");
        Log.e("chat list fragment", "clf u get users - broj" + users.size());


        /*for(User user : users){
            if(user.getId().equals(currentUser.getUid()))
            {
                sender = user;
                break;
            }
        }*/
    }

    private void getMessages()
    {
        MessageRepository messageRepository = new MessageRepository();
        ArrayList<User> usersToShow = new ArrayList<>();
        messageRepository.getByUser(sender.getId())
            .thenAccept(messagesDB -> {
                if (messagesDB != null) {
                    Log.e("messages count ", "msgs  "+usersToShow.size());

                    for(Message mess: messagesDB)
                    {
                        for(User u: users)
                        {
                            if((mess.getRecipientId().equals(u.getId()) || mess.getSenderId().equals(u.getId())) && usersToShow.stream().noneMatch(s -> s.getId().equals(u.getId())))
                            {
                                if(!u.getId().equals(sender.getId()))
                                {
                                    usersToShow.add(u);
                                    Log.e("users to show", "aa "+usersToShow.size());
                                    Log.e("users to show", "aa "+sender.getId());
                                    break;
                                }
                            }
                        }
                        ListView listView = binding.getRoot().findViewById(R.id.chat_list_id);
                        ChatListAdapter adapter = new ChatListAdapter(getActivity(), usersToShow);
                        Log.e("users to show konacna ", "konacna  pomerena ---------------------- "+usersToShow.size());
                        listView.setAdapter(adapter);
                    }
                } else {
                    Log.e("No users and messages found", "aaa");

                }
            });
        Log.e("chat list fragment", "clf 3 get messages");

        /*ListView listView = binding.getRoot().findViewById(R.id.chat_list_id);
        ChatListAdapter adapter = new ChatListAdapter(getActivity(), usersToShow);
        listView.setAdapter(adapter);
        premesti iznad
         */
        Log.e("users to show konacna ", "konacna ------------ "+usersToShow.size());
        Log.e("chat list fragment", "clf 4 posle set adapter - chat list adapter");


    }
}

