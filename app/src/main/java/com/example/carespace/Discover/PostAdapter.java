package com.example.carespace.Discover;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carespace.Exercise.TutorialCardAdapter;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<PostModel> postList;
    private PostAdapter.onItemClickedListener onItemClickedListener;


    public interface onItemClickedListener{
        void onItemClicked(int position);
    }

    public PostAdapter(Context context, ArrayList<PostModel> postList, PostAdapter.onItemClickedListener onItemClickedListener) {
        this.context = context;
        this.postList = postList;
        this.onItemClickedListener = onItemClickedListener;
    }

    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new MyViewHolder(view, onItemClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.MyViewHolder holder, int position) {
        
        PostModel postModel = postList.get(position);
        
        Glide
                .with(context)
                .load(Uri.parse(postModel.getProfilePicUrl()))
                .into(holder.profilePic);
        
        Glide
                .with(context)
                .load(Uri.parse(postModel.getImgUrl()))
                .into(holder.img);
        
        holder.name.setText(postModel.getName());
        holder.title.setText(postModel.getTitle());

        //format timestamp
        String timestamp = postModel.getTimestamp();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String formattedDateTime = DateFormat.format("dd/MM/yyyy K:mm a", calendar).toString();
        holder.timestamp.setText(formattedDateTime);

        GetCommentCount(postModel,holder);

        getLikeCount(postModel, holder);

        likeBtnListener(postModel,holder);

        isTyping(holder);

        publishOnClick(postModel,holder);
    }

    private void GetCommentCount(PostModel postModel, MyViewHolder holder)
    {
        String postID = postModel.getPostID();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Post Comment").child(postID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.numComments.setText(snapshot.getChildrenCount() + " Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLikeCount(PostModel postModel, MyViewHolder holder)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Likes").child(postModel.getPostID());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String likes = snapshot.getChildrenCount() + " likes";
                holder.numLikes.setText(likes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    private void likeBtnListener(PostModel postModel, MyViewHolder holder)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        initialiseLikeBtn(postModel, holder, user);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Likes").child(postModel.getPostID());
        ref.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            removeLikes(ref, user);
                            setUnikeBtn(holder);
                        }
                    });
                }
                else
                {
                    holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addLikes(ref,user);
                            setLikedBtn(holder);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });

    }

    private void initialiseLikeBtn(PostModel postModel, MyViewHolder holder, FirebaseUser user)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Likes").child(postModel.getPostID());
        ref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    setLikedBtn(holder);

                }
                else
                {
                    setUnikeBtn(holder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    private void addLikes(DatabaseReference ref, FirebaseUser user)
    {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",user.getUid());

        ref.child(user.getUid()).setValue(hashMap);
    }

    private void removeLikes(DatabaseReference ref, FirebaseUser user)
    {
        ref.child(user.getUid()).removeValue();
    }

    private void setUnikeBtn(MyViewHolder holder)
    {
        holder.likeBtn.setBackground(context.getDrawable(R.drawable.ic_unlike));
    }

    private void setLikedBtn(MyViewHolder holder)
    {
        holder.likeBtn.setBackground(context.getDrawable(R.drawable.ic_liked));
    }

    private void isTyping(MyViewHolder holder)
    {
        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                holder.publishBtn.setBackgroundResource(R.drawable.rounded_edge_grey);
                holder.publishBtn.setClickable(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                holder.publishBtn.setBackgroundResource(R.drawable.rounded_edge);
                holder.publishBtn.setClickable(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (holder.editText.getText().toString().isEmpty())
                {
                    holder.publishBtn.setBackgroundResource(R.drawable.rounded_edge_grey);
                    holder.publishBtn.setClickable(false);
                }
                else
                {
                    holder.publishBtn.setBackgroundResource(R.drawable.rounded_edge);
                    holder.publishBtn.setClickable(true);
                }
            }
        });
    }

    private void publishOnClick(PostModel postModel,MyViewHolder holder)
    {
        holder.publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Posting your comment...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("uid",user.getUid());
                hashMap.put("photoUrl",user.getPhotoUrl().toString());
                hashMap.put("timestamp",""+System.currentTimeMillis());
                hashMap.put("name",user.getDisplayName());
                hashMap.put("comment",holder.editText.getText().toString());

                String postID = postModel.getPostID();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Post Comment").child(postID);

                ref.push().setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Your comment is posted", Toast.LENGTH_SHORT).show();
                                holder.editText.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Error : Failed to post comment", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }




    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name,timestamp,title,numLikes,numComments;
        CircleImageView profilePic;
        ImageView img;
        ImageButton likeBtn;
        EditText editText;
        Button publishBtn;
        onItemClickedListener onItemClickedListener;

        public MyViewHolder(@NonNull View itemView, onItemClickedListener onItemClickedListener) {
            super(itemView);
            itemView.setOnClickListener(this);

            name = itemView.findViewById(R.id.post_name);
            timestamp = itemView.findViewById(R.id.post_timestamp);
            title = itemView.findViewById(R.id.post_title);
            numLikes = itemView.findViewById(R.id.post_likeNum);
            numComments = itemView.findViewById(R.id.post_commentNum);
            profilePic = itemView.findViewById(R.id.post_profilePic);
            img = itemView.findViewById(R.id.post_img);
            likeBtn = itemView.findViewById(R.id.post_likeBtn);
            editText = itemView.findViewById(R.id.post_ET);
            publishBtn = itemView.findViewById(R.id.publishBtn);
            this.onItemClickedListener = onItemClickedListener;
        }

        @Override
        public void onClick(View view) {
            onItemClickedListener.onItemClicked(getAdapterPosition());
        }
    }
}
