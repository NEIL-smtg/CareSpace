package com.example.carespace.Discover;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class AddPost extends AppCompatActivity {

    //widget
    private EditText editText;
    private AppCompatButton publishBtn;
    private AppCompatButton imgUploaderBtn;
    private ImageView img;
    private ProgressDialog progressDialog;
    private ImageButton back;

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
        setContentView(R.layout.activity_add_post);

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Uploading Video");
        progressDialog.setCanceledOnTouchOutside(false);

        //camera permissions
        cameraPermissions =  new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        img = findViewById(R.id.img);
        editText = findViewById(R.id.editText);
        publishBtn = findViewById(R.id.publishBtn);
        imgUploaderBtn = findViewById(R.id.imgUploaderBtn);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgUploaderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPost();
            }
        });

        checkPublishEligible();

    }

    private void uploadPost()
    {
        progressDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String timestamp = ""+System.currentTimeMillis();
        String filename = "img_" + timestamp;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Post").child(filename);
        storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()){}

                Uri downloadUri = uriTask.getResult();

                if (uriTask.isSuccessful())
                {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Post");
                    String postID = ref.push().getKey();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("uid",user.getUid());
                    hashMap.put("name",user.getDisplayName());
                    hashMap.put("profilePicUrl",user.getPhotoUrl().toString());
                    hashMap.put("timestamp",timestamp);
                    hashMap.put("imgUrl", ""+downloadUri);
                    hashMap.put("title",editText.getText().toString());
                    hashMap.put("postID",postID);

                    ref.child(postID).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddPost.this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddPost.this, "Post Upload Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddPost.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPublishEligible()
    {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                publishBtn.setClickable(false);
                publishBtn.setBackground(ContextCompat.getDrawable(AddPost.this,R.drawable.rounded_edge_grey));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (imgUri == null)
                {
                    publishBtn.setClickable(false);
                    publishBtn.setBackground(ContextCompat.getDrawable(AddPost.this,R.drawable.rounded_edge_grey));
                }
                else
                {
                    publishBtn.setClickable(true);
                    publishBtn.setBackground(ContextCompat.getDrawable(AddPost.this,R.drawable.rounded_edge_orange));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (imgUri == null)
                {
                    publishBtn.setClickable(false);
                    publishBtn.setBackground(ContextCompat.getDrawable(AddPost.this,R.drawable.rounded_edge_grey));
                }
                else
                {
                    if (!editText.getText().toString().isEmpty())
                    {
                        publishBtn.setClickable(true);
                        publishBtn.setBackground(ContextCompat.getDrawable(AddPost.this,R.drawable.rounded_edge_orange));
                    }
                    else
                    {
                        publishBtn.setClickable(false);
                        publishBtn.setBackground(ContextCompat.getDrawable(AddPost.this,R.drawable.rounded_edge_grey));
                    }
                }
            }
        });
    }

    private void pickImage()
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
                Glide.with(AddPost.this).load(imgUri).into(img);
            }
            else if (requestCode == CAMERA_REQUEST_CODE)
            {
                File f = new File(currentPhotoPath);
                imgUri = Uri.fromFile(f);
                fileName = f.getName();

                //img.setImageURI(imgUri);
                Glide.with(this).load(imgUri).into(img);
                if (!editText.getText().toString().isEmpty())
                {
                    publishBtn.setBackground(ContextCompat.getDrawable(AddPost.this,R.drawable.rounded_edge_orange));
                    publishBtn.setClickable(true);
                }

                //add picture to gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(imgUri);
                this.sendBroadcast(mediaScanIntent);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}