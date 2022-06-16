package com.visionDev.digital_time.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.visionDev.digital_time.MainActivity;
import com.visionDev.digital_time.R;

import java.util.ArrayList;
import java.util.List;

/***
* A Service that Observes Campus user in and out
 * on exit triggers a firestore update of user usage and exited location
 * in #onStartCommand takes param intent in which
 * {@value OBSERVED_AREAS} argument is {@link com.google.android.gms.location.Geofence}'s
 *
* **/
public class PlaceTrackerService extends Service {

//    GeofencingClient mGFClient;
    PendingIntent geofencePendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "service Created", Toast.LENGTH_SHORT).show();

//        mGFClient = LocationServices.getGeofencingClient(this);
        Log.i(TAG, "onCreate: ");

        ensureNotificationChannel((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(PLACE_TRACKER_NOTIF_ID,createNotification());
        Toast.makeText(this, "service started " + startId, Toast.LENGTH_SHORT).show();

//        GeofencingRequest gfr = createGeofenceRequest(intent);


//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            stopForeground(true);
//            return  START_NOT_STICKY;
//        }
//        mGFClient.addGeofences(gfr, getGeofencePendingIntent())
//                .addOnSuccessListener(u -> {
//                    Log.d(TAG, "onStartCommand: Successfully Connected geofence listener");
//
//                })
//                .addOnFailureListener(u -> {
//                    Log.d(TAG, "onStartCommand: Failed Connect geofence listener");
//                });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
//        mGFClient.removeGeofences(getGeofencePendingIntent())
//                .addOnSuccessListener(uu -> {
//                    Log.i(TAG, "onDestroy: Removed Geofences");
//                })
//                .addOnFailureListener(uu -> {
//                    Log.i(TAG, "onDestroy: Failed to Remove Geofences");
//                })
//                ;
        super.onDestroy();

    }

    final static  String TAG = "UdageTrackerService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }





   private GeofencingRequest createGeofenceRequest(Intent i){
       List<Geofence> geofences = new ArrayList<>(i.getParcelableArrayListExtra(OBSERVED_AREAS));

      return new GeofencingRequest.Builder()
               .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
               .addGeofences(geofences )
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


    void  ensureNotificationChannel(NotificationManager nm){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         NotificationChannel nc = new NotificationChannel(PLACE_TRACKER_NOTIF_CHANNEL_ID,"Place Tracker Channel",NotificationManager.IMPORTANCE_DEFAULT);
             nm.createNotificationChannel(nc);
        }
    }

    Notification createNotification(){
      return new NotificationCompat.Builder(this,PLACE_TRACKER_NOTIF_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
              .setContentTitle("Your Digital Time")
              .setContentText("We care your screen time")
              .setContentIntent(getAppOpenIntent())

              .setCategory(Notification.CATEGORY_SERVICE)
                .setAutoCancel(true)
               .build();
    }

    PendingIntent getAppOpenIntent(){
        Intent intent = new Intent(this, MainActivity.class);
        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }
    public static final String OBSERVED_AREAS = "geofences";
    public  static int PLACE_TRACKER_NOTIF_ID = 18092002;
    public  static final String PLACE_TRACKER_NOTIF_CHANNEL_ID = "com.visionDev.place_tracker_channel";

}
