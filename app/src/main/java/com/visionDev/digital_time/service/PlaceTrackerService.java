package com.visionDev.digital_time.service;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/***
* A Service that Observes Campus user in and out
 * on exit triggers a firestore update of user usage and exited location
 * in #onStartCommand takes param intent in which
 * {@value OBSERVED_AREAS} argument is {@link com.google.android.gms.location.Geofence}'s
 *
* **/
public class PlaceTrackerService extends Service {

    GeofencingClient mGFClient;
    PendingIntent geofencePendingIntent;

    @Override
    public void onCreate() {

        mGFClient = LocationServices.getGeofencingClient(this);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GeofencingRequest gfr = createGeofenceRequest(intent);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopForeground(true);
            return  START_NOT_STICKY;
        }
        mGFClient.addGeofences(gfr, getGeofencePendingIntent())
                .addOnSuccessListener(u -> {
                    Log.d(TAG, "onStartCommand: Successfully Connected geofence listener");

                })
                .addOnFailureListener(u -> {
                    Log.d(TAG, "onStartCommand: Failed Connect geofence listener");
                });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mGFClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(uu -> {
                    Log.i(TAG, "onDestroy: Removed Geofences");
                })
                .addOnFailureListener(uu -> {
                    Log.i(TAG, "onDestroy: Failed to Remove Geofences");
                })
                ;
        super.onDestroy();
    }

    final static  String TAG = "UdageTrackerService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }





   private GeofencingRequest createGeofenceRequest(Intent i){
      return new GeofencingRequest.Builder()
               .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
               .addGeofences( new ArrayList<>(i.getParcelableArrayListExtra(OBSERVED_AREAS)))
               .build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeoFenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }


    public static final String OBSERVED_AREAS = "geofences";

}
