package com.example.carespace.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    //widgets
    private CircleImageView img;
    private TextView changePicBtn;
    private EditText username,email,contactNum,address,password,confirmPassword;
    private ImageButton back,saveBtn;
    private RelativeLayout confirmPasswordLayout;
    private ProgressDialog progressDialog;

    //vars
    private String oldpass,oldemail;

    //firebase
    private FirebaseUser user;

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
        setContentView(R.layout.activity_edit_profile);

        init();
        setupView();
        isPasswordTyping();
    }

    private void isPasswordTyping()
    {
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                confirmPasswordLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    private void setupView()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User").child(user.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel model = snapshot.getValue(UserModel.class);

                //set up vars
                Glide.with(EditProfile.this).load(model.getImgUrl()).into(img);
                username.setText(model.getName());
                email.setText(model.getEmail());
                contactNum.setText(model.getContactNum());
                address.setText(model.getAddress());
                password.setText(model.getPassword());
                imgUrl = model.getImgUrl();

                oldpass = model.getPassword();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("db error",error.getMessage());
            }
        });
    }

    private void init()
    {
        //camera permissions
        cameraPermissions =  new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        user = FirebaseAuth.getInstance().getCurrentUser();

        img = findViewById(R.id.img);
        changePicBtn = findViewById(R.id.changePicBtn);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        contactNum = findViewById(R.id.contactNum);
        address = findViewById(R.id.address);
        saveBtn = findViewById(R.id.saveBtn);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.Confirmpassword);
        confirmPasswordLayout = findViewById(R.id.ConfirmpasswordLayout);
        back = findViewById(R.id.back);

        changePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePicOnClick();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = checkValidation();

                if (valid)
                {
                    updateEmailPassword();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void updateEmailPassword()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Updating profile...");
        progressDialog.show();

        oldemail = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(oldemail,oldpass);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    user.updateEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                user.updatePassword(oldpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            updateImgDisplayName();
                                        }
                                        else
                                        {
                                            Toast.makeText(EditProfile.this, "Something went wrong, try again later.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(EditProfile.this, "Something went wrong, try again later.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(EditProfile.this, "Authentication wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void updateImgDisplayName()
    {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(username.getText().toString())
                .setPhotoUri(imgUri)
                .build();

        user.updateProfile(profileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateToFirebase();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Something went wrong, try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateToFirebase()
    {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",user.getUid());
        hashMap.put("name",username.getText().toString());
        hashMap.put("imgUrl",imgUrl);
        hashMap.put("password",password.getText().toString());
        hashMap.put("email",email.getText().toString());
        hashMap.put("contactNum",contactNum.getText().toString());
        hashMap.put("address",address.getText().toString());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User").child(user.getUid());
        ref.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                Toast.makeText(EditProfile.this, "Updated your profile", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(EditProfile.this, "Fail to edit your profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkValidation()
    {
        boolean validName,validEmail,validContact, validPassword, validAddress;

        if (username.getText().toString().isEmpty() || username.getText().toString().equals(" "))
        {
            username.setError("Name should not be empty");
            validName = false;
        }
        else
        {
            validName = true;
        }

        if (contactNum.getText().toString().isEmpty() || contactNum.getText().toString().equals(" "))
        {
            contactNum.setError("Contact should not be empty");
            validContact = false;
        }
        else
        {
            validContact = true;
        }

        if (email.getText().toString().isEmpty() || email.getText().toString().equals(" ") || !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches())
        {
            email.setError("Enter a valid e-mail");
            validEmail = false;
        }
        else
        {
            validEmail = true;
        }

        if (address.getText().toString().isEmpty() || address.getText().toString().equals(" "))
        {
            address.setError("Please describe your problem");
            validAddress = false;
        }
        else
        {
            validAddress = true;
        }

        if (password.getText().toString().isEmpty() || password.getText().toString().equals(" "))
        {
            password.setError("Password cannot be empty");
            validPassword = false;
        }
        else if (!password.getText().toString().equals(confirmPassword.getText().toString()))
        {
            confirmPassword.setError("Password are not same");
            validPassword = false;
        }
        else
        {
            validPassword = true;
        }

        return validName && validContact && validEmail && validAddress && validPassword;

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
                    Glide.with(EditProfile.this).load(imgUrl).into(img);
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}