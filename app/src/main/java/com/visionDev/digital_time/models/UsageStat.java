package com.visionDev.digital_time.models;

import androidx.annotation.NonNull;
import androidx.work.Data;

import com.google.firebase.firestore.GeoPoint;
import com.visionDev.digital_time.service.UsageStatsUploadWorker;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class UsageStat implements Serializable {
     String place;
     Map<String,Long> usageStats;

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    GeoPoint location;

    public String getPlace() {
        return place;
    }


    public Map<String, Long> getUsageStats() {
        return usageStats;
    }

    public void setUsageStats(Map<String, Long> usageStats) {
        this.usageStats = usageStats;
    }

    public UsageStat() {
    }

    @NonNull
    @Override
    public String toString() {
        return "UsageStat{" +
                "place='" + place + '\'' +
                ", usageStats=" + usageStats +
                ", location=" + location +
                '}';
    }

    public void setPlace(String place) {
        this.place = place;
    }


}
