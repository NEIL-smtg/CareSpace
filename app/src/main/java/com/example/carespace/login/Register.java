package com.example.carespace.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carespace.Gallery.GalleryCameraFragmentContainer;
import com.example.carespace.Permission.Permissions;
import com.example.carespace.Profile.EditProfile;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity {

    //widgets
    private Button register_btn;
    private ImageButton back;
    private EditText email,username,password,Rpassword, contactNum, address;
    private CircleImageView picture;

    //firebase
    FirebaseAuth firebaseAuth;

    //permission
    private String[] cameraPermissions;

    //vars
    private Uri imgUri = null;
    private String imgUrl;
    private String currentPhotoPath,fileName;

    //constant
    private static final int PICK_PICTURE = 200;
    private static final int CAMERA_REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        registerOnClick();
        picOnClick();

    }

    private void init()
    {
        //camera permissions
        cameraPermissions =  new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        imgUrl = "http://java2s.com/style/logo.png";

        register_btn =  findViewById(R.id.register_btn);
        back =  findViewById(R.id.back);
        email =  findViewById(R.id.email);
        username =  findViewById(R.id.username);
        password =  findViewById(R.id.password);
        Rpassword =  findViewById(R.id.comfirm_password);
        picture =  findViewById(R.id.picture);
        contactNum = findViewById(R.id.contactNum);
        address = findViewById(R.id.address);

        firebaseAuth = FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this,Login.class));
                finish();
            }
        });
    }

    private void registerOnClick()
    {
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkLengthValidation() && checkEmptyString() && isValidEmail() && checkpwSimilarity())
                {
                    //add progress dialog
                    ProgressDialog dialog = new ProgressDialog(Register.this);
                    dialog.setMessage("Creating account...");
                    dialog.show();

                    //if input are all valid, register the user in firebase
                    firebaseAuth.createUserWithEmailAndPassword(email.getEditableText().toString(),password.getEditableText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult)
                                {
                                    uploadToFirebase();
                                    dialog.dismiss();
                                    Toast.makeText(Register.this, "Account is created successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),LogintoExistingAcc.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    dialog.dismiss();
                                    Toast.makeText(Register.this, "Error !! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private void picOnClick()
    {
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                changePicOnClick();
            }
        });
    }

    private void uploadToFirebase()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",user.getUid());
        hashMap.put("name",username.getText().toString());
        hashMap.put("imgUrl",imgUrl);
        hashMap.put("password",password.getText().toString());
        hashMap.put("email",email.getText().toString());
        hashMap.put("contactNum",contactNum.getText().toString());
        hashMap.put("address",address.getText().toString());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User").child(user.getUid());
        ref.setValue(hashMap);
    }

    private boolean checkLengthValidation()
    {
        if(password.getEditableText().toString().length()<6  )
        {
            password.setError("Password must be more than 6 characters !!");
            return false;
        }

        if(Rpassword.getEditableText().toString().length()<6 )
        {
            Rpassword.setError("Password must be more than 6 characters !!");
            return false;
        }

        return true;
    }

    private boolean checkEmptyString()
    {
        String em = email.getEditableText().toString();
        String usname = username.getEditableText().toString();

        if (em.trim().equals("") || usname.trim().equals(""))
        {
            email.setError("email and username should not be empty !!");
            return false;
        }

        if (usname.trim().equals(""))
        {
            username.setError("email and username should not be empty !!");
            return false;
        }

        return true;
    }

    private boolean isValidEmail()
    {
        CharSequence target = email.getEditableText().toString();

        if (!(!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()))
        {
            email.setError("invalid email");
            return false;
        }

        return true;
    }

    //check similarity of 2 password
    private boolean checkpwSimilarity()
    {
        String pw = password.getEditableText().toString();
        String rpw = Rpassword.getEditableText().toString();

        if (!pw.equals(rpw))
        {
            password.setError("Password is not the same !!");
            Rpassword.setError("Password is not the same !!");
            return false;
        }
        return true;
    }

    private void changePicOnClick()
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

        String timestamp = new SimpleDateFormat("yy-MM-dd hh:mm a").format(Calendar.getInstance().getTime());
        String filename = "img_" + timestamp;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("User").child(filename);
        storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()){}

                Uri downloadUri = uriTask.getResult();

                if (uriTask.isSuccessful())
                {
                    imgUrl = downloadUri.toString();
                    Glide.with(Register.this).load(imgUrl).into(picture);
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}