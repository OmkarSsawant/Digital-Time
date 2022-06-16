package com.visionDev.digital_time.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.visionDev.digital_time.repository.FirestoreManager;

import java.util.List;

public class GeoFenceBroadcastReceiver extends BroadcastReceiver {
    long entryTime=-1L;
    long exitTime=-1L;
    String placeName;
    FirestoreManager fm;
    SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {


        if(fm==null){
            fm = new FirestoreManager(context);
        }

        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("current.campus", Context.MODE_PRIVATE);
        }
        GeofencingEvent gfe = GeofencingEvent.fromIntent(intent);

        if(gfe == null || gfe.hasError()){
            return;
        }
        List<Geofence> triggeredGFs = gfe.getTriggeringGeofences();
        switch (gfe.getGeofenceTransition()){
            case Geofence
                    .GEOFENCE_TRANSITION_ENTER :
            {
                Log.i(TAG, "onReceive: Entered "+triggeredGFs.get(0).getRequestId());
                entryTime = System.currentTimeMillis();
               placeName =   triggeredGFs.get(0).getRequestId();
               sharedPreferences.edit()
                       .putString("placeName",placeName)
                       .apply();

                break;
            }
            case Geofence.GEOFENCE_TRANSITION_EXIT:
            {
                Log.i(TAG, "onReceive: Exited"+triggeredGFs.get(0).getRequestId() );
                exitTime = System.currentTimeMillis();
                updateStats(context);
                break;
            }
            default: {
                break;
            }
        }
    }

    private void updateStats(Context ctx) {
////       Data input = UsageStatsUploadWorker.buildData(placeName);
//        OneTimeWorkRequest updateStatsReq = new  OneTimeWorkRequest.Builder(UsageStatsUploadWorker.class)
//                .addTag("UPDATE_STATS")
//                .setInputData(input)
//                .build();
//        WorkManager.getInstance(ctx)
//                .enqueue(updateStatsReq);

    }

    public  static String TAG = "GeoFenceListener";


}
