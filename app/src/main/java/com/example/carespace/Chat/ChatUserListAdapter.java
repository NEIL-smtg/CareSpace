package com.example.carespace.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carespace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserListAdapter extends RecyclerView.Adapter<ChatUserListAdapter.myViewHolder> {

    private onItemClickListener onItemClickListener;
    private Context context;
    private ArrayList<ChatUserListModel> chatList;
    private ArrayList<String> oldMsg;

    //constants
    private static final String FIREBASE_IMG_LINK = "https://firebasestorage.googleapis.com/v0/b/carespace-3173c.appspot.com";

    public ChatUserListAdapter(ChatUserListAdapter.onItemClickListener onItemClickListener, Context context, ArrayList<ChatUserListModel> chatList, ArrayList<String> oldMsg) {
        this.onItemClickListener = onItemClickListener;
        this.context = context;
        this.chatList = chatList;
        this.oldMsg = oldMsg;
    }

    public interface onItemClickListener{
        void onChatClicked(int position);
    }

    @NonNull
    @Override
    public ChatUserListAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user_item, parent, false);
        return new myViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserListAdapter.myViewHolder holder, int position) {

        ChatUserListModel model = chatList.get(position);

        holder.name.setText(model.getReceiverName());
        Glide.with(context).load(model.getReceiverImg()).into(holder.img);
        holder.timestamp.setText(model.getTimestamp());

        if (model.getLastMessage().contains(FIREBASE_IMG_LINK))
        {
            String msg = "[PICTURE]";
            holder.lastmsg.setText(msg);
        }
        else
        {
            holder.lastmsg.setText(model.getLastMessage());
        }

        //notification
//        if (oldMsg.size() != 0) {
//            if (model.getLastMessage().equals(oldMsg.get(position))) {
//                holder.notificationBadge.setNumber(1);
//            } else {
//                holder.notificationBadge.clear();
//            }
//        }
        holder.notificationBadge.clear();
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        onItemClickListener onItemClickListener;
        CircleImageView img;
        TextView name,timestamp,lastmsg;
        NotificationBadge notificationBadge;

        public myViewHolder(@NonNull View itemView, ChatUserListAdapter.onItemClickListener onItemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.onItemClickListener = onItemClickListener;

            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
            timestamp = itemView.findViewById(R.id.timestamp);
            lastmsg = itemView.findViewById(R.id.lastmsg);
            notificationBadge = itemView.findViewById(R.id.notification_badge);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onChatClicked(getAdapterPosition());
        }
    }
}
