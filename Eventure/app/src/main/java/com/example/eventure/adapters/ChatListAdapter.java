package com.example.eventure.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.eventure.R;
import com.example.eventure.fragments.organizer.ChatFragment;
import com.example.eventure.model.User;
import com.example.eventure.settings.FragmentTransition;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends ArrayAdapter<User> {
    private List<User> users = new ArrayList<>();
    private FragmentActivity context;
    private FragmentManager fragmentManager;


    /*public ChatListAdapter(FragmentActivity context, ArrayList<User> u){
        super(co);
        //super(fragmentManager.getPrimaryNavigationFragment().getContext(), R.layout.chat_card, u);
        this.users = u;
        this.fragmentManager = fragmentManager;
    }*/

    public ChatListAdapter(FragmentActivity context, ArrayList<User> u){
        super(context, R.layout.chat_card, u);
        this.users = u;
        this.context = context;
        this.fragmentManager = context.getSupportFragmentManager();
    }

    @Override
    public int getCount() {
        return users.size();
    }


    @Nullable
    @Override
    public User getItem(int position) {
        return users.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User e = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_card,
                    parent, false);
        }

        TextView name = convertView.findViewById(R.id.name);
        name.setText(e.getFirstName() + " " + e.getLastName());

        LinearLayout card = (LinearLayout) convertView.findViewById(R.id.card);
        card.setOnClickListener(v -> {
            //FragmentTransition.to(ChatFragment.newInstance(e.getId()), context,
             //       true, R.id.chat);
            ChatFragment fragment = ChatFragment.newInstance(e.getId());
            fragmentManager.beginTransaction()
                    .replace(R.id.activity_home_base, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        Log.e("chat list adapterrrr", "cla 1");

        return convertView;
    }
}
