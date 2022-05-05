package com.example.carespace.VideoActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class VideoUploaderActivity extends AppCompatActivity {

    //widgets
    private EditText title_video;
    private VideoView videoView;
    private Button btn_uploadvideo;
    private FloatingActionButton pickVideoFab,back;
    private ProgressDialog progressDialog;


    //constant
    private static final int VIDEO_PICK_GALLERY = 100;
    private static final int VIDEO_PICK_CAMERA = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

    //Permission
    private String[] cameraPermissions;

    //URI of pick video
    private Uri videoUri = null;
    private String title;

    //thumbnail
    private byte[] data;
    private Uri thumbnailUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_uploader);


        //widgets
        title_video = findViewById(R.id.title_video);
        videoView = findViewById(R.id.video_view);
        btn_uploadvideo = findViewById(R.id.btn_uploadVideo);
        pickVideoFab = findViewById(R.id.pickVideoFab);
        back = findViewById(R.id.btnback_video);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Uploading Video");
        progressDialog.setCanceledOnTouchOutside(false);

        //camera permissions
        cameraPermissions =  new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};


        btn_uploadvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = title_video.getText().toString().trim();
                if (TextUtils.isEmpty(title))
                {
                    Toast.makeText(VideoUploaderActivity.this, "Title is required...", Toast.LENGTH_SHORT).show();
                }
                else if (videoUri == null)
                {
                    Toast.makeText(VideoUploaderActivity.this, "Pick a video before you can upload...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    uploadVideotoFirebase();
                }
            }
        });

        //handle pick video from gallery
        pickVideoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPickDialog();
            }
        });
    }

    private void uploadVideotoFirebase()
    {
        //show progress
        progressDialog.show();

        //time stamp
        String timestamp = ""+System.currentTimeMillis();

        //file path and name in firebase
        String filePathandName = "Videos/" + "video_" + timestamp;

        //file path of thumbnail
        String thumbnailPath = "Thumbnail/" + "picture_" + timestamp;

        //storage reference of thumbnail
        StorageReference thumbref = FirebaseStorage.getInstance().getReference(thumbnailPath);

        //upload thumbnail
        thumbref.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //get uri of thumbnail if sucess
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()){}
                        thumbnailUri = uriTask.getResult();
                        if (thumbnailUri == null)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(VideoUploaderActivity.this, "Upload failed...", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });


        //storage reference of video
        StorageReference videoref = FirebaseStorage.getInstance().getReference(filePathandName);

        //upload video
        videoref.putFile(videoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                        //get uri of video if success
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()){}
                        Uri downloadUri = uriTask.getResult();
                        if (uriTask.isSuccessful())
                        {
                            //uri is received
                            //now we can add video details to firebase
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("id",timestamp);
                            hashMap.put("title",title);
                            hashMap.put("timestamp",timestamp);
                            hashMap.put("videoUrl", ""+downloadUri);
                            hashMap.put("thumbnailUrl", ""+thumbnailUri);

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Videos");
                            reference.child(timestamp)
                                    .setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //video details added to db
                                            progressDialog.dismiss();
                                            Toast.makeText(VideoUploaderActivity.this, "Video uplaoded", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(VideoUploaderActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(VideoUploaderActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void videoPickDialog()
    {
        //options to display in dialog
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
                                videoPickCamera();
                            }
                        }
                        else if (choice==1)
                        {
                            //gallery clicked
                            videoPickGallery();
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
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;

        return (result1 && result2);
    }

    private void videoPickGallery()
    {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Videos"), VIDEO_PICK_GALLERY);
    }

    private void videoPickCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent,VIDEO_PICK_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                        videoPickCamera();
                    }
                    else
                    {
                        Toast.makeText(this, "Camera and storage permission are required", Toast.LENGTH_SHORT).show();
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK)
        {
            if (requestCode == VIDEO_PICK_GALLERY)
            {
                videoUri = data.getData();
                setVideoView();
                createThumbnail();
            }
            else if (requestCode == VIDEO_PICK_CAMERA)
            {
                videoUri = data.getData();
                setVideoView();
                createThumbnail();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setVideoView()
    {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);

        videoView.setVideoURI(videoUri);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
    }

    private void createThumbnail()
    {
        //get screenshot/thumbnail
        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
        mMMR.setDataSource(VideoUploaderActivity.this, videoUri);
        Bitmap bitmap = mMMR.getFrameAtTime(new Long(1000));

        //convert bitmap to jpg, data to generate uri to upload to firebase
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        data = stream.toByteArray();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();    //go to previous activity by clicking back button on action bar
        return super.onSupportNavigateUp();
    }
}