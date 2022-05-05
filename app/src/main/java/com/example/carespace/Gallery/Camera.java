package com.example.carespace.Gallery;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carespace.Permission.Permissions;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera extends Fragment {

    //constant code
    private static final int CAMERA_FRAGMENT = 1;
    private static final int CAMERA_REQUEST_CODE = 102;

    //vars
    String currentPhotoPath, fileName;

    //widgets
    Button btnLaunchCamera;
    ImageView pictureView;
    TextView next;
    ImageButton userPic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        btnLaunchCamera = (Button) view.findViewById(R.id.btnLaunchCamera);
        pictureView = (ImageView) view.findViewById(R.id.tmpProfilePic);
        next = (TextView) view.findViewById(R.id.camera_nxt);


        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchCamera();
            }
        });


        return view;
    }


    private void LaunchCamera()
    {
        if (((GalleryCameraFragmentContainer)getActivity()).getCurrentTabNumber() == CAMERA_FRAGMENT)
        {
           dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == getActivity().RESULT_OK)
        {
            Toast.makeText(getActivity(), "pic taken", Toast.LENGTH_SHORT).show();

        }
    }
}