package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.eventure.R;
import com.example.eventure.fragments.organizer.ChatFragment;
import com.example.eventure.model.Message;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>{
    private List<Message> messages;
    private FragmentManager fragmentManager;
    private ChatFragment chatFragment;

    public ChatListAdapter(List<Message> messages, FragmentManager fragmentManager, ChatFragment chatFragment) {
        this.messages = messages;
        this.fragmentManager = fragmentManager;
        this.chatFragment = chatFragment;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card, parent, false);
        return new ChatListAdapter.ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        TextView content;
        TextView status;
        Button markAsReadButton;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            content = itemView.findViewById(R.id.content);
            status = itemView.findViewById(R.id.status);
            markAsReadButton = itemView.findViewById(R.id.mark_as_read_button);
        }

    }

}
