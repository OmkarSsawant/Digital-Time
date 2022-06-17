package com.visionDev.digital_time.service;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.GeoPoint;
import com.visionDev.digital_time.models.Campus;
import com.visionDev.digital_time.models.UsageStat;
import com.visionDev.digital_time.repository.FirestoreManager;
import com.visionDev.digital_time.repository.SharedPrefsManager;
import com.visionDev.digital_time.utils.Constants;
import com.visionDev.digital_time.utils.FutureListener;
import com.visionDev.digital_time.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class UsageStatsUploadWorker extends Worker{



    FirestoreManager firestoreManager;
    SharedPrefsManager sharedPrefsManager;


    public UsageStatsUploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        firestoreManager = new FirestoreManager(context);
        sharedPrefsManager =    new SharedPrefsManager(getApplicationContext());

    }

    @NonNull
    @Override
    public Result doWork() {
        Utils.getCurrentLocationAndCampus(getApplicationContext(),firestoreManager,new FutureListener<Pair<Campus,Location>>() {
            @Override
            public void onSuccess(Pair<Campus,Location> result) {

                update(result.first, result.second);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });


        return  Result.success();
    }

    void update(@Nullable Campus campus, Location mLocation){
        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Map<String, UsageStats> statsMap=  usageStatsManager.queryAndAggregateUsageStats(System.currentTimeMillis() - Constants.MILLISECONDS_OF_15,System.currentTimeMillis());

        Map<String,Long> intervalStats = new HashMap<>();
        String area = Constants.PLACE_OTHER;
        if (campus != null) {
            area = campus.getName();
        }
        UsageStat prevStat = sharedPrefsManager.getUsageStat(area);
        if(prevStat!=null && prevStat.getUsageStats()!=null){
            for (String pkgName: statsMap.keySet()){
                UsageStats stats = statsMap.get(pkgName);
                long appUsedTime =  stats.getTotalTimeInForeground() ;
                if(prevStat.getUsageStats().containsKey(pkgName)){
                    appUsedTime += prevStat.getUsageStats().get(pkgName);
                }
                Log.i(TAG, "doWork:     "+ pkgName + "       was used for      " + appUsedTime + " milliseconds") ;
                if(appUsedTime!=0)
                    intervalStats.put(pkgName,appUsedTime);
            }

        }else{
            for (String pkgName: statsMap.keySet()){
                UsageStats stats = statsMap.get(pkgName);
                long appUsedTime = stats.getTotalTimeInForeground() ;

                Log.i(TAG, "doWork:     "+ pkgName + "       was used for      " + appUsedTime + " milliseconds") ;
                if(appUsedTime!=0)
                    intervalStats.put(pkgName,appUsedTime);
            }

        }


        UsageStat stat = new UsageStat();
        stat.setPlace(area);
        stat.setLocation(new GeoPoint(mLocation.getLatitude(),mLocation.getLongitude()));
        stat.setUsageStats(intervalStats);
        sharedPrefsManager
                .saveUsageStat(stat);

    }


    public static final String AREA = "area";
    public static final String LOC_LAT = "in";
    public static final String LOC_LANG = "out";
    public static final String TAG = "UsageStatsStoreWorker";
    private static final String IS_LOCAL = "local_update";

}