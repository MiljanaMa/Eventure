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
import com.example.eventure.adapters.ChatListAdapter;
import com.example.eventure.model.Message;
import com.example.eventure.repositories.MessageRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {


    private static final List<Message> messages = new ArrayList<>();
    private View view;
    private MessageRepository messageRepository;
    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    private void setUpMessages() {
        RecyclerView messagesListView = view.findViewById(R.id.chat_fragment);
        messagesListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ChatListAdapter chatListAdapter = new ChatListAdapter(messages, requireActivity().getSupportFragmentManager(), ChatFragment.this);
        messagesListView.setAdapter(chatListAdapter);
    }

    public void loadMessages() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            final List<Message> finalMessages = new ArrayList<>();
            messageRepository.getAllByRecipient(currentUser.getUid()).thenAccept(data -> {
                if (data != null) {
                    setUpMessages();
                } else {
                    Log.e("Chat fragment", "Failed to retrieve messages from the database");
                }
            });
        }
    }
}