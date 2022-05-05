package com.example.carespace.Exercise;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.format.DateFormat;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<CommentModel> commentList;
    private DatabaseReference ref;


    public CommentAdapter(Context context, ArrayList<CommentModel> commentList, DatabaseReference ref) {
        this.context = context;
        this.commentList = commentList;
        this.ref = ref;
    }

    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position) {

        CommentModel commentModel = commentList.get(position);

        Glide
                .with(context)
                .load(Uri.parse(commentModel.getPhotoUrl()))
                .into(holder.profilePic);

        holder.comment.setText(commentModel.getComment());
        holder.name.setText(commentModel.getName());

        //format timestamp
        String timestamp = commentModel.getTimestamp();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String formattedDateTime = DateFormat.format("dd/MM/yyyy K:mm a", calendar).toString();
        holder.timestamp.setText(formattedDateTime);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getUid().equals(commentModel.getUid()))
        {
            holder.edit.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);

            setEditDeleteComment(holder,position);
        }
    }

    private void setEditDeleteComment(MyViewHolder holder, int position)
    {
        String original_comment = commentList.get(position).getComment();

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
                editedComment.setText(commentList.get(position).getComment());
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
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    for (DataSnapshot ds : snapshot.getChildren())
                                    {
                                        if (ds.child("comment").getValue().toString().equals(original_comment))
                                        {
                                            HashMap<String,Object> hashMap = new HashMap<>();
                                            hashMap.put("comment",editedComment.getText().toString());
                                            ds.getRef().updateChildren(hashMap);
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            commentList.get(position).setComment(editedComment.getText().toString());
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

                commentList.remove(position);
                notifyItemRemoved(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView comment,timestamp,name;
        CircleImageView profilePic;
        ImageButton edit,delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            comment = itemView.findViewById(R.id.comment);
            timestamp = itemView.findViewById(R.id.comment_timestamp);
            name = itemView.findViewById(R.id.comment_name);
            profilePic = itemView.findViewById(R.id.comment_profilePic);
            edit = itemView.findViewById(R.id.editBtn);
            delete = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
