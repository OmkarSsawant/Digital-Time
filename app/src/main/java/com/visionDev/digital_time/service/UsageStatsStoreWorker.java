package com.visionDev.digital_time.service;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.visionDev.digital_time.utils.UsageStatsUtils;

import java.util.Map;

public class UsageStatsStoreWorker extends Worker{


    String area;
    SharedPreferences mSF;


    public UsageStatsStoreWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        area = workerParams.getInputData().getString(AREA);
        mSF = getApplicationContext().getSharedPreferences("com.visionDev.usage_stats",Context.MODE_PRIVATE);

    }

    @NonNull
    @Override
    public Result doWork() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Map<String, UsageStats> statsMap=  usageStatsManager.queryAndAggregateUsageStats(UsageStatsUtils.getTodayDayStart(),System.currentTimeMillis());



        Log.i(TAG, "doWork: Stats when user at "+area);
        for (Map.Entry<String, UsageStats> AppStat: statsMap.entrySet()){
              String appPackage = AppStat.getKey();
              UsageStats stats = AppStat.getValue();
              long appUsedTime = stats.getTotalTimeInForeground();
            Log.i(TAG, "doWork:     "+ appPackage + "       was used for      " + appUsedTime + " milliseconds") ;

        }

        return  Result.success();
    }



    private static final String AREA = "user.area";
    private static final String TAG = "UsageStatsStoreWorker";
}