package com.visionDev.digital_time.service;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.GeoPoint;
import com.visionDev.digital_time.models.UsageStat;
import com.visionDev.digital_time.repository.FirestoreManager;
import com.visionDev.digital_time.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class UsageStatsUploadWorker extends Worker{


    String area;
    GeoPoint loc;
    FirestoreManager firestoreManager;

    public UsageStatsUploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        firestoreManager = new FirestoreManager(context);
        Data data = workerParams.getInputData();
       area =  data.getString(AREA);
        loc = new GeoPoint(data.getDouble(LOC_LAT,0), data.getDouble(LOC_LANG,0));
    }

    @NonNull
    @Override
    public Result doWork() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Map<String, UsageStats> statsMap=  usageStatsManager.queryAndAggregateUsageStats(Utils.getTodayDayStart(),System.currentTimeMillis());


        //   current app usage - (day_start:entry_app_usage)
//        Map<String, UsageStats> prevStatsMap=  usageStatsManager.queryAndAggregateUsageStats(Utils.getTodayDayStart(),startTime-1_000);
        Map<String,Long> intervalStats = new HashMap<>();
        Log.d(TAG, "doWork: Stats when user at "+area + " : "+ statsMap.size() + " Apps Stats for "+ Utils.getTodayDayStart() + " to "+System.currentTimeMillis());

        for (String pkgName: statsMap.keySet()){
            UsageStats stats = statsMap.get(pkgName);
            long appUsedTime = stats.getTotalTimeInForeground() ;
//            if(prevStatsMap.containsKey(pkgName)){
//                appUsedTime -= prevStatsMap.get(pkgName).getTotalTimeInForeground();
//            }
            Log.i(TAG, "doWork:     "+ pkgName + "       was used for      " + appUsedTime + " milliseconds") ;
            if(appUsedTime!=0)
            intervalStats.put(pkgName,appUsedTime);
        }


        UsageStat stat = new UsageStat();
        stat.setPlace(area);
        stat.setLocation(loc);
        stat.setUsageStats(intervalStats);
//        Log.i(TAG, "doWork: "+ String.join(",",intervalStats.keySet()));
        firestoreManager.saveStat(stat,getApplicationContext().getContentResolver()).addOnSuccessListener(v-> Log.i(TAG, "doWork: \"Updated Stats\""))
                .addOnFailureListener(e-> Log.i(TAG, "doWork: \"Updated Failed Stats\""));

        return  Result.success();
    }



   public static Data buildData(String place, Location geoPoint){
        return  new Data.Builder()
                .putString(AREA,place)
                .putDouble(LOC_LAT, geoPoint.getLatitude())
                .putDouble(LOC_LANG, geoPoint.getLongitude())
                .build();
    }

    public static final String AREA = "area";
    public static final String LOC_LAT = "in";
    public static final String LOC_LANG = "out";
    public static final String TAG = "UsageStatsStoreWorker";
}