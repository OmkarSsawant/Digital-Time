package com.visionDev.digital_time.service;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.visionDev.digital_time.utils.UsageStatsUtils;

import java.util.HashMap;
import java.util.Map;

public class UsageStatsStoreWorker extends Worker{


    String area;
    SharedPreferences mSF;
    long startTime;
    long endTime;

    public UsageStatsStoreWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
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
        Map<String, UsageStats> statsMap=  usageStatsManager.queryAndAggregateUsageStats(UsageStatsUtils.getTodayDayStart(),System.currentTimeMillis());

        Log.d(TAG, "doWork: Stats when user at "+area);

        //   current app usage - (day_start:entry_app_usage)
        Map<String, UsageStats> prevStatsMap=  usageStatsManager.queryAndAggregateUsageStats(UsageStatsUtils.getTodayDayStart(),startTime-1000);
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


        return  Result.success();
    }


    static Data buildData(long start,long end,String place){
        return  new Data.Builder()
                .putString(AREA,place)
                .putLong(IN_TIME,start)
                .putLong(OUT_TIME,end)
                .build();
    }

    private static final String AREA = "user.area";
    private static final String IN_TIME = "user.in";
    private static final String OUT_TIME = "user.out";
    private static final String TAG = "UsageStatsStoreWorker";
}