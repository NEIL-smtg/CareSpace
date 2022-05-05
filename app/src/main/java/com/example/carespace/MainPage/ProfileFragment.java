package com.example.carespace.MainPage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carespace.Discover.PostModel;
import com.example.carespace.Profile.DailyInfomation;
import com.example.carespace.Profile.EditProfile;
import com.example.carespace.Profile.UserModel;
import com.example.carespace.Profile.myDiscoverView;
import com.example.carespace.Profile.myFavouritesView;
import com.example.carespace.Profile.myFollowingView;
import com.example.carespace.R;
import com.example.carespace.login.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    //widgets
    private CircleImageView img;
    private CardView editBtn;
    private Button logoutBtn, infoBtn;
    private TextView name, following, discover, favourites;
    private RelativeLayout followingLayout, discoverLayout, favouritesLayout;

    //vars
    private int discoverCount, followingCount, favouritesCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        init(view);
        setupView();

        return view;
    }

    private void init(View view)
    {
        followingCount = discoverCount = favouritesCount = 0;

        img = view.findViewById(R.id.img);
        editBtn = view.findViewById(R.id.editBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        name = view.findViewById(R.id.name);
        following = view.findViewById(R.id.following);
        discover = view.findViewById(R.id.discover);
        favourites = view.findViewById(R.id.favourite);
        followingLayout = view.findViewById(R.id.followingLayout);
        discoverLayout = view.findViewById(R.id.discoverLayout);
        favouritesLayout = view.findViewById(R.id.favouriteLayout);
        infoBtn = view.findViewById(R.id.infoBtn);

        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), DailyInfomation.class));
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditProfile.class));
            }
        });

        followingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), myFollowingView.class));
            }
        });

        discoverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), myDiscoverView.class));
            }
        });

        favouritesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), myFavouritesView.class));
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
            }
        });
    }

    private void setupView()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
        {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User").child(user.getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel model = snapshot.getValue(UserModel.class);

                    if (model==null)
                    {
                        Glide.with(getActivity()).load(getResources().getDrawable(R.drawable.ic_launcher_foreground)).into(img);
                    }
                    else {
                        Glide.with(getActivity()).load(model.getImgUrl()).into(img);
                        name.setText(model.getName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("db error",error.getMessage());
                }
            });


            getDiscoverCount(user);
            getFollowingCount(user);
            getFavouritesCount(user);
        }
    }

    private void getFavouritesCount(FirebaseUser user)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Favourites").child(user.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favourites.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    private void getFollowingCount(FirebaseUser user)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Following").child(user.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    private void getDiscoverCount(FirebaseUser user)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Post");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    PostModel model = ds.getValue(PostModel.class);
                    if (model.getUid().equals(user.getUid()))
                    {
                        discoverCount++;
                    }
                }

                discover.setText(discoverCount+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error", error.getMessage());
            }
        });
    }


}