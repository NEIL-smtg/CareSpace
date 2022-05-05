package com.example.carespace.Shop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carespace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemReviewAdapter extends RecyclerView.Adapter<ItemReviewAdapter.myViewHolder> {

    private Context context;
    private ArrayList<ItemReviewModel> reviewList;
    private DatabaseReference ref;

    public ItemReviewAdapter(Context context, ArrayList<ItemReviewModel> reviewList, DatabaseReference ref) {
        this.context = context;
        this.reviewList = reviewList;
        this.ref = ref;
    }

    @NonNull
    @Override
    public ItemReviewAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_item, parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemReviewAdapter.myViewHolder holder, int position) {

        ItemReviewModel reviewModel = reviewList.get(position);

        Glide
                .with(context)
                .load(Uri.parse(reviewModel.getPhotoUrl()))
                .into(holder.img);

        holder.comment.setText(reviewModel.getReview());
        holder.name.setText(reviewModel.getName());

        holder.timestamp.setText(reviewModel.getTimestamp());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getUid().equals(reviewModel.getUid()))
        {
            holder.edit.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);

            setEditDeleteComment(holder,position);
        }
    }

    private void setEditDeleteComment(ItemReviewAdapter.myViewHolder holder, int position)
    {
        String original_comment = reviewList.get(position).getReview();
        String reviewID = reviewList.get(position).getReviewID();

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Edit Comment");

                final EditText editedComment = new EditText(context);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                editedComment.setLayoutParams(lp);
                editedComment.setText(reviewList.get(position).getReview());
                alertDialog.setView(editedComment);
                alertDialog.setIcon(R.drawable.ic_edit);

                alertDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (editedComment.getText().toString().isEmpty())
                        {
                            editedComment.setError("Should not be empty !!");
                            dialogInterface.dismiss();
                        }
                        else
                        {
                            //edit the comment on firebase
                            //ref.orderByChild("comment").equalTo(original_comment);
                            ref.child(reviewID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.child("review").getValue().toString().equals(original_comment))
                                    {
                                        HashMap<String,Object> hashMap = new HashMap<>();
                                        hashMap.put("review",editedComment.getText().toString());
                                        snapshot.getRef().updateChildren(hashMap);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            reviewList.get(position).setReview(editedComment.getText().toString());
                            notifyItemChanged(position);

                        }
                    }
                });

                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                alertDialog.show();

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ref.orderByChild("comment").equalTo(original_comment);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren())
                        {
                            if (ds.child("comment").getValue().toString().equals(original_comment))
                            {
                                ds.getRef().removeValue();
                                break;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                reviewList.remove(position);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView comment,timestamp,name;
        CircleImageView img;
        ImageButton edit,delete;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            comment = itemView.findViewById(R.id.comment);
            timestamp = itemView.findViewById(R.id.timestamp);
            name = itemView.findViewById(R.id.name);
            img = itemView.findViewById(R.id.img);
            edit = itemView.findViewById(R.id.editBtn);
            delete = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
