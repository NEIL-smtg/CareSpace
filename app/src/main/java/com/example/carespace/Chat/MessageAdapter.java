package com.example.carespace.Chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.carespace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.myViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private static final String FIREBASE_IMG_LINK = "https://firebasestorage.googleapis.com/v0/b/carespace-3173c.appspot.com";

    private Context context;
    private ArrayList<ChatModel> chatList;
    private String imgUrl;
    private FirebaseUser user;

    public MessageAdapter(Context context, ArrayList<ChatModel> chatList, String imgUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imgUrl = imgUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new myViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new myViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.myViewHolder holder, int position) {

        ChatModel model = chatList.get(position);

        final ImagePopup imagePopup = new ImagePopup(context);
        imagePopup.setWindowHeight(800);
        imagePopup.setWindowWidth(800);
        imagePopup.setBackgroundColor(Color.BLACK);
        imagePopup.setFullScreen(true);
        imagePopup.setHideCloseIcon(true);
        imagePopup.setImageOnClickClose(true);

        if (model.getMessage().contains(FIREBASE_IMG_LINK))
        {
            holder.imgholder.setVisibility(View.VISIBLE);
            holder.imgTimestamp.setVisibility(View.VISIBLE);
            holder.show_message.setVisibility(View.GONE);
            holder.timestamp.setVisibility(View.GONE);

            Glide.with(context).load(imgUrl).into(holder.img);
            holder.imgTimestamp.setText(model.getTimestamp());
            Glide.with(context).asBitmap().load(model.getMessage()).centerCrop().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Drawable dr = new BitmapDrawable(resource);
                    holder.imgMsg.setImageDrawable(dr);
                    holder.imgMsg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            });

            //pop up image when image message on click
            holder.imgMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imagePopup.initiatePopup(holder.imgMsg.getDrawable());
                    imagePopup.viewPopup();
                }
            });
        }
        else
        {
            holder.imgholder.setVisibility(View.GONE);
            holder.imgTimestamp.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.VISIBLE);
            holder.timestamp.setVisibility(View.VISIBLE);

            holder.show_message.setText(model.getMessage());
            Glide.with(context).load(imgUrl).into(holder.img);
            holder.timestamp.setText(model.getTimestamp());
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView show_message,timestamp, imgTimestamp;
        ImageView imgMsg;
        CircleImageView img;
        CardView imgholder;
        NotificationBadge notificationBadge;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            img = itemView.findViewById(R.id.img);
            timestamp = itemView.findViewById(R.id.timestamp);
            imgMsg = itemView.findViewById(R.id.imgMsg);
            imgTimestamp = itemView.findViewById(R.id.imgTimestamp);
            imgholder = itemView.findViewById(R.id.imgholder);

        }
    }

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(user.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }
}
