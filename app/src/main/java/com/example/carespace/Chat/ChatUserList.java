package com.example.carespace.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carespace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;

public class ChatUserList extends AppCompatActivity implements ChatUserListAdapter.onItemClickListener{

    //widgets
    private ImageButton back;
    private RecyclerView rv;

    //get user who has conversation with 'me'
    private ArrayList<ChatUserListModel> chatList;

    //firebase
    private FirebaseUser user;

    //old msg
    private ArrayList<String> oldMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user_list);

        init();
        setupRV();
    }

    private void setupRV()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ChatList").child(user.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                oldMsg.clear();

                if (chatList.size() != 0)
                {
                    for (int i =0 ; i<chatList.size() ; i++)
                    {
                        oldMsg.add(chatList.get(i).getLastMessage());
                    }
                }

                chatList.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    ChatUserListModel model = ds.getValue(ChatUserListModel.class);
                    chatList.add(model);
                }

                ChatUserListAdapter adapter = new ChatUserListAdapter(ChatUserList.this, ChatUserList.this, chatList, oldMsg);
                rv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });

    }


    private void init()
    {
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        chatList = new ArrayList<>();
        oldMsg = new ArrayList<>();

        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onChatClicked(int position) {

        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);

        ChatUserListModel cm = chatList.get(position);

        intent.putExtra("targetID",cm.getReceiverID());
        intent.putExtra("targetName",cm.getReceiverName());
        intent.putExtra("targetImg",cm.getReceiverImg());

        startActivity(intent);
    }


}