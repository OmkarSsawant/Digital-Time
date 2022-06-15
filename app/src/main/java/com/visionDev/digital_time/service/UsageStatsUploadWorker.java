package com.visionDev.digital_time.service;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.visionDev.digital_time.models.IntervalUsageStat;
import com.visionDev.digital_time.repository.FirestoreManager;
import com.visionDev.digital_time.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class UsageStatsUploadWorker extends Worker{


    String area;
    SharedPreferences mSF;
    long startTime;
    long endTime;
    FirestoreManager firestoreManager;

    public UsageStatsUploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        firestoreManager = new FirestoreManager(context);
        Data data = workerParams.getInputData();
       area =  data.getString(AREA);
       startTime = data.getLong(IN_TIME,-1L);
        endTime = data.getLong(OUT_TIME,-1L);
        mSF = getApplicationContext().getSharedPreferences("com.visionDev.usage_stats",Context.MODE_PRIVATE);

    }

    @NonNull
    @Override
    public Result doWork() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Map<String, UsageStats> statsMap=  usageStatsManager.queryAndAggregateUsageStats(Utils.getTodayDayStart(),System.currentTimeMillis());

        Log.d(TAG, "doWork: Stats when user at "+area);

        //   current app usage - (day_start:entry_app_usage)
        Map<String, UsageStats> prevStatsMap=  usageStatsManager.queryAndAggregateUsageStats(Utils.getTodayDayStart(),startTime-1000);
        Map<String,Long> intervalStats = new HashMap<>();

        for (String pkgName: statsMap.keySet()){
            UsageStats stats = statsMap.get(pkgName);
            long appUsedTime = stats.getTotalTimeInForeground() ;
            if(prevStatsMap.containsKey(pkgName)){
                appUsedTime -= prevStatsMap.get(pkgName).getTotalTimeInForeground();
            }
            Log.i(TAG, "doWork:     "+ pkgName + "       was used for      " + appUsedTime + " milliseconds") ;
            intervalStats.put(pkgName,appUsedTime);
        }


        IntervalUsageStat stat = new IntervalUsageStat();
        stat.setPlaceEntryTime(startTime);
        stat.setPlaceExitTime(endTime);
        stat.setPlace(area);
        stat.setUsageStats(intervalStats);
        firestoreManager.saveStat(stat,getApplicationContext().getContentResolver()).addOnSuccessListener(v->{

        })
                .addOnFailureListener(e->{

                });

        return  Result.success(buildResult(intervalStats));
    }


    Data buildResult(Map<String,Long> r){
        Data.Builder b =  new Data.Builder()
                .putAll(buildData(startTime,endTime,area));
        for (Map.Entry<String,Long> s : r.entrySet()){
            b.putLong(s.getKey(),s.getValue());
        }
                return b.build();
    }

   public static Data buildData(Long start,Long end,String place){
        return  new Data.Builder()
                .putString(AREA,place)
                .putLong(IN_TIME,start)
                .putLong(OUT_TIME,end)
                .build();
    }

    public static final String AREA = "area";
    public static final String IN_TIME = "in";
    public static final String OUT_TIME = "out";
    public static final String TAG = "UsageStatsStoreWorker";
}