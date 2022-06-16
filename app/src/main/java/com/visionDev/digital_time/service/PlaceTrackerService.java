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
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.visionDev.digital_time.MainActivity;
import com.visionDev.digital_time.MainActivityViewModel;
import com.visionDev.digital_time.R;
import com.visionDev.digital_time.models.UsageStat;
import com.visionDev.digital_time.repository.FirestoreManager;
import com.visionDev.digital_time.repository.SharedPrefsManager;
import com.visionDev.digital_time.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/***
* A Service that Observes Campus user in and out
 * on exit triggers a firestore update of user usage and exited location
 * in #onStartCommand takes param intent in which
 * {@value OBSERVED_AREAS} argument is {@link com.google.android.gms.location.Geofence}'s
 * After Each 15 minute locally the usage stat data is mapped with user location
* **/
public class PlaceTrackerService extends Service {



    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        ensureNotificationChannel((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));
        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(getApplicationContext());
        FirestoreManager firestoreManager = new FirestoreManager(getApplicationContext());
    //Note : Each day at 11:58 a new service is created so  firestore is updated
            for (UsageStat s:
                    sharedPrefsManager.getUsageStats()) {
                firestoreManager.saveStat(s,getApplicationContext().getContentResolver()).addOnSuccessListener(v-> Log.i(TAG, "doWork: \"Updated Stats\""))
                        .addOnFailureListener(e-> Log.i(TAG, "doWork: \"Updated Failed Stats\""));
            }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(PLACE_TRACKER_NOTIF_ID,createNotification());
        PeriodicWorkRequest recurringUpdateRequest = new PeriodicWorkRequest
                .Builder(UsageStatsUploadWorker.class,15, TimeUnit.MINUTES)
                .setConstraints(
                        new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build())
                .build();
        WorkManager.getInstance(getApplication())
                .enqueueUniquePeriodicWork(Constants.USAGE_UPDATER, ExistingPeriodicWorkPolicy.REPLACE,recurringUpdateRequest);
        return START_STICKY;
    }

    final static  String TAG = "UdageTrackerService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
