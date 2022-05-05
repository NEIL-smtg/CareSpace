package com.example.carespace.Nearby;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.carespace.MainPage.MainPage;
import com.example.carespace.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class NearbySearch extends AppCompatActivity {

    //widgets
    private Spinner spType;
    private Button btFind,back;

    //map
    private SupportMapFragment supportMapFragment;
    private GoogleMap map;

    //location
    private FusedLocationProviderClient fusedLocationProviderClient;

    //vars
    private double currentLat = 0, currentLong = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_search);

        spType = (Spinner) findViewById(R.id.sp_type);

        back = (Button) findViewById(R.id.btnbackNearby) ;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NearbySearch.this, MainPage.class));
                finish();
            }
        });

        btFind = (Button) findViewById(R.id.bt_find);
        btFind.setClickable(false); //unclickable when location is disabled to avoid rum time exceeded

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        //initialize array of place type
        String[] placeTypeList = new String[]{"hospital", "clinic"};
        //initialize array of place name
        String[] placeNameList = new String[]{"Hospital", "Clinic"};

        //set adapter on spinner
        spType.setAdapter(new ArrayAdapter<>(NearbySearch.this, android.R.layout.simple_spinner_dropdown_item, placeNameList));

        //initialize fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //show google location dialog
        createLocationRequest();

        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get selected position of spinner
                int i = spType.getSelectedItemPosition();
                //initialize url
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" + //url
                        "?location=" + currentLat +","+ currentLong +    //longitude and latitude
                        "&radius=5000" +        //radius
                        "&types=" + placeTypeList[i] +
                        "&sensor=true" +
                        "&key=" + getResources().getString(R.string.google_map_key);

                //execute place task method to download json data
                new PlaceTask().execute(url);
            }
        });

    }

    private void createLocationRequest() {
        //show google location dialog
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10);
        mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new
                LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);


       if (!checkLocationEnabled())
       {
           Task<LocationSettingsResponse> task=LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

           task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
               @Override
               public void onComplete(Task<LocationSettingsResponse> task) {
                   try {
                       LocationSettingsResponse response = task.getResult(ApiException.class);
                       // All location settings are satisfied. The client can initialize location
                       // requests here.

                   } catch (ApiException exception) {
                       switch (exception.getStatusCode()) {
                           case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                               // Location settings are not satisfied. But could be fixed by showing the
                               // user a dialog.
                               try {
                                   // Cast to a resolvable exception.
                                   ResolvableApiException resolvable = (ResolvableApiException) exception;
                                   // Show the dialog by calling startResolutionForResult(),
                                   // and check the result in onActivityResult().
                                   resolvable.startResolutionForResult(
                                           NearbySearch.this,
                                           101);
                               } catch (IntentSender.SendIntentException e) {
                                   // Ignore the error.
                               } catch (ClassCastException e) {
                                   // Ignore, should be an impossible error.
                               }
                               break;
                           case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                               // Location settings are not satisfied. However, we have no way to fix the
                               // settings so we won't show the dialog.
                               break;
                       }
                   }
               }
           });
       }
       else
       {
           getCurrentLocation();
       }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case 101:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        btFind.setClickable(true);
                        getCurrentLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        createLocationRequest();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }

    private boolean checkLocationEnabled()
    {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception ex){
            Toast.makeText(NearbySearch.this, ""+ex.getMessage(), Toast.LENGTH_SHORT).show();
        };

        return gps_enabled;
    }


    private void getCurrentLocation() {

        //check permission
        if (ActivityCompat.checkSelfPermission(NearbySearch.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //when permission granted
            //initialize task location

            Task<Location> task = fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                @NonNull
                @Override
                public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                    return null;
                }

                @Override
                public boolean isCancellationRequested() {
                    return false;
                }
            });
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null)
                    {
                        //when location is not equal to null
                        //get current latitude and longitude
                        currentLat = location.getLatitude();
                        currentLong = location.getLongitude();

                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                //when map ready
                                map = googleMap;
                                //zoom current location on map
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat,currentLong),10));

                                //add current location marker
                                LatLng curlatLng = new LatLng(currentLat,currentLong);

                                Marker curLoc = map.addMarker(
                                        new MarkerOptions()
                                                .position(curlatLng)
                                                .title("You are here")
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            }
                        });
                    }
                }
            });

        } else {
            //when permission denied
            //request permission
            ActivityCompat.requestPermissions(NearbySearch.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    private class PlaceTask extends AsyncTask<String,Integer,String>
    {
        @Override
        protected String doInBackground(String... strings)
        {
            String data = null;

            try {
                //initialize data
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException
    {
        //initialize url
        URL url = new URL(string);
        //initialize connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //Connect connection
        connection.connect();
        //initialize input stream
        InputStream stream = connection.getInputStream();
        //initialize buffer reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        //initialize string bulder
        StringBuilder builder = new StringBuilder();
        //initialize string var
        String line = "";

        while ((line= reader.readLine()) != null)
        {
            //append line
            builder.append(line);
        }

        //get append data
        String data = builder.toString();
        //close reader
        reader.close();

        return data;
    }

    private class ParserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>>
    {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            //create json parser class
            JsonParser jsonParser = new JsonParser();
            //initialize hash map list
            List<HashMap<String,String>> mapList = null;
            JSONObject object = null;

            try {
                //initialize json object
                object = new JSONObject(strings[0]);
                //parse json object
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            //clear map
            map.clear();

            for (int i = 0; i < hashMaps.size(); i++)
            {
                HashMap<String,String> hashMapList = hashMaps.get(i);

                //get long and latitude
                double lat = Double.parseDouble(hashMapList.get("lat"));
                double lng = Double.parseDouble(hashMapList.get("lng"));
                String name = hashMapList.get("name");

                //add marker on map of place nearby
                LatLng latLng = new LatLng(lat,lng);

                Marker marker = map.addMarker(
                        new MarkerOptions()
                                .position(latLng)
                                .title(name)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));


                //add current location marker
                LatLng curlatLng = new LatLng(currentLat,currentLong);

                Marker curLoc = map.addMarker(
                        new MarkerOptions()
                                .position(curlatLng)
                                .title("You are here")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }
        }
    }
}