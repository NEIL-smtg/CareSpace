package com.example.carespace.Permission;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissions {

    private static final int VERIFY_PERMISSION_REQUEST =1;

    public String[] PERMISSIONS= {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public String[] CAMERA_PERMISSION = {
            Manifest.permission.CAMERA
    };

    private Activity activity;

    public Permissions(Activity activity)
    {
        this.activity=activity;
    }

    public void verifyPermission()
    {
        ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS,
                VERIFY_PERMISSION_REQUEST
        );
    }

    public boolean checkPermissionArray()
    {
        for (int i = 0; i < PERMISSIONS.length; i++)
        {
            String check = PERMISSIONS[i];
            if (!checkPermission(check))
            {
                return false;
            }
        }
        return true;
    }

    public boolean checkPermission(String check)
    {
        int permissionRequest = ActivityCompat.checkSelfPermission(activity,check);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }

        return true;
    }

}
