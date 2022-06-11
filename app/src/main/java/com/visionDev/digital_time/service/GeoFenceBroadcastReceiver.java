package com.visionDev.digital_time.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.visionDev.digital_time.models.IntervalUsageStat;
import com.visionDev.digital_time.repository.FirestoreManager;

import java.util.List;

public class GeoFenceBroadcastReceiver extends BroadcastReceiver {
    long entryTime;
    long exitTime;
    String placeName;
    FirestoreManager fm;
    @Override
    public void onReceive(Context context, Intent intent) {

        if(fm==null){
            fm = new FirestoreManager(context);
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
       Data input = UsageStatsWorker.buildData(entryTime,exitTime,placeName);
        OneTimeWorkRequest updateStatsReq = new  OneTimeWorkRequest.Builder(UsageStatsWorker.class)
                .addTag("UPDATE_STATS")
                .setInputData(input)
                .build();
        WorkManager.getInstance(ctx)
                .enqueue(updateStatsReq);
        WorkManager.getInstance(ctx).getWorkInfosByTagLiveData("UPDATE_STATS")
                .observeForever(workInfos -> {
                    WorkInfo wi = workInfos.get(workInfos.size()-1);
                    if (wi.getState() == WorkInfo.State.SUCCEEDED){
                        IntervalUsageStat stat = IntervalUsageStat.fromData(wi.getOutputData());
                        fm.saveStat(stat,ctx.getContentResolver())
                                .addOnSuccessListener(s->{
                                    Log.i(TAG, "updateStats: SAVED STAT to Firestore");
                                })
                                .addOnFailureListener(f -> {
                                    Log.i(TAG, "updateStats: Failed to SAVE STAT to Firestore");
                                });
                    }
                });
    }

    public  static String TAG = "GeoFenceListener";


}
