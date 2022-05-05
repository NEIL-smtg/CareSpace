package com.example.carespace.Chat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carespace.Discover.AddPost;
import com.example.carespace.Notification.FcmNotificationsSender;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    //widgets
    private ImageButton back, sendBtn, uploadImgBtn;
    private TextView targetName;
    private RecyclerView rvMessage;
    private EditText typing;

    //notification title for new message
    private static final String NOTIFICATION_TITLE = "NEW MESSAGE";
    private static final String FIREBASE_IMG_LINK = "https://firebasestorage.googleapis.com/v0/b/carespace-3173c.appspot.com";


    //vars for adapter
    private MessageAdapter adapter;
    private ArrayList<ChatModel> msgList;

    //incoming vars
    private String targetID, strTargetName, targetImg;

    //current user
    private FirebaseUser user;

    //permission
    private String[] cameraPermissions;

    //vars
    Uri imgUri = null;
    String currentPhotoPath,fileName;

    //constant
    private static final int PICK_PICTURE = 200;
    private static final int CAMERA_REQUEST_CODE = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getIncomingIntent();
        init();
        sendOnClick();
    }

    private void getIncomingIntent()
    {
        targetID = getIntent().getStringExtra("targetID");
        strTargetName = getIntent().getStringExtra("targetName");
        targetImg = getIntent().getStringExtra("targetImg");
    }

    private void init()
    {
        //camera permissions
        cameraPermissions =  new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        uploadImgBtn = findViewById(R.id.uploadImgBtn);
        uploadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadOnClick();
            }
        });
        sendBtn = findViewById(R.id.sendBtn);
        targetName = findViewById(R.id.targetName);
        rvMessage = findViewById(R.id.rvMessage);
        typing = findViewById(R.id.typing);

        user = FirebaseAuth.getInstance().getCurrentUser();
        targetName.setText(strTargetName);

        rvMessage.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        //linearLayoutManager.setReverseLayout(true);
        rvMessage.setLayoutManager(linearLayoutManager);
        //readMessage(user.getUid(),targetID,targetImg);
    }

    private void sendOnClick()
    {
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msg = typing.getText().toString();

                if (!msg.isEmpty())
                {
                    String timestamp = new SimpleDateFormat("yy-MM-dd hh:mm a").format(Calendar.getInstance().getTime());

                    sendMessage(user.getUid(), targetID, msg, user.getDisplayName(), strTargetName, user.getPhotoUrl().toString(), targetImg, timestamp);
                    typing.setText(""); // clear the edit text
                }
                else
                {
                    Toast.makeText(ChatActivity.this, "You can't send a empty message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //get all the chat messages and display it using adapter
        readMessage(user.getUid(), targetID, targetImg);
    }

    private void uploadOnClick()
    {
        String[] options = {"Camera","Gallery"};

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Video From")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int choice) {
                        if (choice==0)
                        {
                            //camera clicked
                            if (!checkCameraPermision())
                            {
                                requestCameraPermission();
                            }
                            else
                            {
                                imgPickCamera();
                            }
                        }
                        else if (choice==1)
                        {
                            //gallery clicked
                            imgPickGallery();
                        }
                    }
                }).show();
    }

    private void requestCameraPermission()
    {
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermision()
    {
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;

        return (result1 && result2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0)
                {
                    //check if permission is allowed
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted)
                    {
                        imgPickCamera();
                    }
                    else
                    {
                        Toast.makeText(this, "Camera and storage permission are required", Toast.LENGTH_SHORT).show();
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void imgPickGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_PICTURE);
    }

    private void imgPickCamera()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            // Create the File where the photo should go
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.carespace.android.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile
                (
                        imageFileName,      /* prefix */
                        ".jpg",        /* suffix */
                        storageDir          /* directory */
                );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK)
        {
            if (requestCode == PICK_PICTURE)
            {
                imgUri = data.getData();
                uploadImgtoFirebase();
            }
            else if (requestCode == CAMERA_REQUEST_CODE)
            {
                File f = new File(currentPhotoPath);
                imgUri = Uri.fromFile(f);
                fileName = f.getName();

                uploadImgtoFirebase();

                //add picture to gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(imgUri);
                this.sendBroadcast(mediaScanIntent);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImgtoFirebase()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String timestamp = new SimpleDateFormat("yy-MM-dd hh:mm a").format(Calendar.getInstance().getTime());
        String filename = "img_" + timestamp;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Chat").child(filename);
        storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()){}

                Uri downloadUri = uriTask.getResult();

                if (uriTask.isSuccessful())
                {
                    String msg = ""+downloadUri;
                    sendMessage(user.getUid(), targetID, msg, user.getDisplayName(), strTargetName, user.getPhotoUrl().toString(), targetImg, timestamp );
                    readMessage(user.getUid(), targetID, targetImg);
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String sender, String receiver, String message, String senderName, String receiverName, String senderImg, String receiverImg, String timestamp)
    {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        //upload chat messages to firebase
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("senderName", senderName);
        hashMap.put("receiverName", receiverName);
        hashMap.put("senderImg", senderImg);
        hashMap.put("receiverImg",receiverImg);
        hashMap.put("timestamp",timestamp);

        ref.child("Chats").push().setValue(hashMap);


        //upload userlist for current user  in chatroom
        HashMap<String, Object> map = new HashMap<>();
        map.put("receiverID",receiver);
        map.put("lastMessage",message);
        map.put("timestamp",timestamp);
        map.put("receiverImg",receiverImg);
        map.put("receiverName",receiverName);

        ref.child("ChatList").child(sender).child(receiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                {
                    ref.child("ChatList").child(sender).child(receiver).setValue(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
        ref.child("ChatList").child(sender).child(receiver).updateChildren(map);


        //upload userlist for target user  in chatroom
        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("receiverID",sender);
        map2.put("lastMessage",message);
        map2.put("timestamp",timestamp);
        map2.put("receiverImg",senderImg);
        map2.put("receiverName",senderName);

        DatabaseReference refe = FirebaseDatabase.getInstance().getReference("ChatList").child(receiver).child(sender);
        refe.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                {
                    refe.setValue(map2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
        refe.updateChildren(map2);

        //get token for target user
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        tokens.child(targetID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Token tok = snapshot.getValue(Token.class);
                if (!tok.getToken().isEmpty())
                {
                    //send the notification to target user
                    //identify whether the message is an image
                    if (message.contains(FIREBASE_IMG_LINK))
                    {
                        sendNotification(tok.getToken(), NOTIFICATION_TITLE, senderName + ": [Image]");
                    }
                    else
                    {
                        sendNotification(tok.getToken(), NOTIFICATION_TITLE, senderName + ": " + message);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    private void sendNotification(String token, String title, String message)
    {
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token, title, message, getApplicationContext(), ChatActivity.this);
        notificationsSender.SendNotifications();
    }

    private void readMessage(String myID, String targetID, String imgUrl)
    {
        msgList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                msgList.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    ChatModel model = ds.getValue(ChatModel.class);
                    if ((model.getReceiver().equals(myID) && model.getSender().equals(targetID)) ||
                            model.getReceiver().equals(targetID) && model.getSender().equals(myID)
                    )
                    {
                        msgList.add(model);
                    }
                }

                //sort with timestamp
                Collections.sort(msgList, new Comparator<ChatModel>() {

                    DateFormat f = new SimpleDateFormat("yy-MM-dd hh:mm a");

                    @Override
                    public int compare(ChatModel t0, ChatModel t1) {
                        try {
                            return f.parse(t0.getTimestamp()).compareTo(f.parse(t1.getTimestamp()));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });


                adapter = new MessageAdapter(ChatActivity.this, msgList, imgUrl);
                rvMessage.setAdapter(adapter);

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        rvMessage.scrollToPosition(msgList.size()-1);
//                    }
//                }, 200);

                rvMessage.scrollToPosition(msgList.size()-1);  //always show newest message
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }
}