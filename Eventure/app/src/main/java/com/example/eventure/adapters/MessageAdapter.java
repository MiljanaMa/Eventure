package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.eventure.R;
import com.example.eventure.model.Message;
import com.example.eventure.model.User;
import com.example.eventure.model.enums.MessageStatus;
import com.example.eventure.repositories.MessageRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private List<Message> messages = new ArrayList<>();
    private FragmentActivity context;
    private User sender = new User();
    private User recipient = new User();
    private FirebaseUser currentUser;


    public MessageAdapter(FragmentActivity c, ArrayList<Message> u, User s, User r){
        super(c, R.layout.message_holder_other, u);
        this.messages = u;
        this.context = c;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser.getUid().equals(s.getId()))
        {
            this.sender = s;
            this.recipient = r;
        }else{
            this.sender = r;
            this.recipient = s;
        }

    }

    @Override
    public int getCount() {
        return messages.size();
    }


    @Nullable
    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Message e = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_holder_other,
                    parent, false);
        }

        if(e.getStatus().equals(MessageStatus.UNREAD) && e.getRecipientId().equals(sender.getId()))
        {
            e.setStatus(MessageStatus.READ);
            MessageRepository messageRepository = new MessageRepository();
            messageRepository.update(e);
        }

        View l = convertView.findViewById(R.id.l_mess);
        TextView text = l.findViewById(R.id.message_text);
        text.setText(e.getContent());

        TextView time = l.findViewById(R.id.message_time);
        time.setText(e.getDate() + "  " + e.getTime());

        ImageView seen = l.findViewById(R.id.seen);
        if(e.getStatus().equals(MessageStatus.UNREAD))
            seen.setVisibility(View.INVISIBLE);
        LinearLayout linearLayout = convertView.findViewById(R.id.lin_layout);
        if(e.getSenderId().equals(sender.getId()))
        {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            linearLayout.setLayoutParams(layoutParams);
        }else{
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            linearLayout.setLayoutParams(layoutParams);
        }

        return convertView;
    }
}
