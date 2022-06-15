package com.visionDev.digital_time.models;

import androidx.work.Data;

import com.visionDev.digital_time.service.UsageStatsUploadWorker;

import java.util.HashMap;
import java.util.Map;

public class IntervalUsageStat {
     String place;
     long placeEntryTime;
     long placeExitTime;
     Map<String,Long> usageStats;


    public String getPlace() {
        return place;
    }

    public long getPlaceEntryTime() {
        return placeEntryTime;
    }

    public long getPlaceExitTime() {
        return placeExitTime;
    }

    public Map<String, Long> getUsageStats() {
        return usageStats;
    }

    public void setUsageStats(Map<String, Long> usageStats) {
        this.usageStats = usageStats;
    }

    public IntervalUsageStat() {
    }

    public IntervalUsageStat(String place, long placeEntryTime, long placeExitTime) {
        this.place = place;
        this.placeEntryTime = placeEntryTime;
        this.placeExitTime = placeExitTime;
    }


    public void setPlace(String place) {
        this.place = place;
    }

    public void setPlaceEntryTime(long placeEntryTime) {
        this.placeEntryTime = placeEntryTime;
    }

    public void setPlaceExitTime(long placeExitTime) {
        this.placeExitTime = placeExitTime;
    }

    public static IntervalUsageStat fromData(Data data){
       IntervalUsageStat s = new IntervalUsageStat();
        s.setPlace(data.getString(UsageStatsUploadWorker.AREA));
        s.setPlaceEntryTime(data.getLong(UsageStatsUploadWorker.IN_TIME,-1L));
        s.setPlaceExitTime(data.getLong(UsageStatsUploadWorker.OUT_TIME,-1L));
        final HashMap<String,Long> stats = new HashMap<>();
        for (String key:
                data.getKeyValueMap().keySet()) {
            if (key.contains(".")){
                //It is package name
                stats.put(key,data.getLong(key,-1L));
            }
        }
        s.setUsageStats(stats);
        return s;
        }


}
