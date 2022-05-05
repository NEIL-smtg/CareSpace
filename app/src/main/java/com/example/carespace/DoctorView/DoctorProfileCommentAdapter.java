package com.example.carespace.DoctorView;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carespace.R;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorProfileCommentAdapter extends RecyclerView.Adapter<DoctorProfileCommentAdapter.myViewHolder> {

    private Context context;
    private ArrayList<ReviewModel> reviewList;

    public DoctorProfileCommentAdapter(Context context, ArrayList<ReviewModel> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public DoctorProfileCommentAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_review_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorProfileCommentAdapter.myViewHolder holder, int position) {

        ReviewModel reviewModel = reviewList.get(position);

        Glide.with(context).load(reviewModel.getPhotoUrl()).into(holder.img);
        holder.name.setText(reviewModel.getName());
        holder.comment.setText(reviewModel.getReview());
        holder.ratingBar.setRating(Float.parseFloat(reviewModel.getRating()));

        //format timestamp
        String ts = reviewModel.getTimestamp();
        holder.timestamp.setText(ts);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView name, timestamp, comment;
        RatingBar ratingBar;
        CircleImageView img;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            timestamp = itemView.findViewById(R.id.timestamp);
            comment = itemView.findViewById(R.id.comment);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            img = itemView.findViewById(R.id.img);
        }
    }
}
