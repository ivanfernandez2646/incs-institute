package com.dam.t08p01.repositorio;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.dam.t08p01.R;
import com.dam.t08p01.vista.MainActivity;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

public class LocGoogle {

    private static LocGoogle locGoogle;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    //LocationCallback del ActivityIncs
    private LocationCallback locationCallback;


    private Activity activity;
    public static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    public static final int REQUEST_CHECK_SETTINGS = 2;

    private LocGoogle() {

    }

    public static LocGoogle getInstance(){
        if(locGoogle == null){
            locGoogle = new LocGoogle();
        }
        return locGoogle;
    }

    public void initLocGoogle(Activity activity, LocationCallback locationCallback){
        this.activity = activity;
        this.locationCallback = locationCallback;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        int intervaloLecturas = Integer.parseInt(pref.getString(activity.getApplicationContext().getResources().getString(R.string.Loc_interval_key),
                pref.getString(activity.getApplicationContext().getResources().getString(R.string.Loc_interval_default),"")));

        int intervaloDesplazamiento = Integer.parseInt(pref.getString(activity.getApplicationContext().getResources().getString(R.string.Loc_desplazamiento_key),
                pref.getString(activity.getApplicationContext().getResources().getString(R.string.Loc_desplazamiento_default),"")));

        if(intervaloDesplazamiento == 0){
            intervaloDesplazamiento = 2;
        }

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(intervaloLecturas * 1000);
        locationRequest.setFastestInterval(intervaloLecturas * 1000);
        locationRequest.setSmallestDisplacement(intervaloDesplazamiento);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Comprobamos
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(activity,task_OnSuccessListener);
        task.addOnFailureListener(activity,task_OnFailureListener);
    }

    public void startLocationUpdates(LocationCallback locationCallback) {

        ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);

        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessFineLocationApproved) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            ActivityCompat.requestPermissions(activity, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                    },
                    PERMISSION_ACCESS_FINE_LOCATION);
        }
    }

    public void stopLocationUpdates(LocationCallback locationCallback){
        if(fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private OnSuccessListener task_OnSuccessListener = new OnSuccessListener<LocationSettingsResponse>(){
        @Override
        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
            startLocationUpdates(locationCallback);
        }
    };

    private OnFailureListener task_OnFailureListener = new OnFailureListener(){
        @Override
        public void onFailure(@NonNull Exception e) {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(activity,
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        }
    };
}

