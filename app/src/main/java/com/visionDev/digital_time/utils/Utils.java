package com.visionDev.digital_time.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import androidx.core.app.ActivityManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.visionDev.digital_time.models.Campus;
import com.visionDev.digital_time.models.UsageStat;
import com.visionDev.digital_time.repository.FirestoreManager;
import com.visionDev.digital_time.repository.SharedPrefsManager;
import com.visionDev.digital_time.service.PlaceTrackerService;
import com.visionDev.digital_time.ui.screens.AppsDisplayFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.annotation.Nullable;

public class Utils {
    public  static long getTodayDayStart(){
       Date today=  Calendar.getInstance().getTime();
             Calendar calendar =   Calendar.getInstance();
                            calendar.setTime(today);
                            calendar.set(Calendar.HOUR_OF_DAY,0);
                            calendar.set(Calendar.MINUTE,0);
                            calendar.set(Calendar.SECOND,1);
      return  calendar.getTime().getTime();
    }

    public static UsageStat getUsageStat(String place,List<UsageStat> stats){
        for (UsageStat s:
                stats) {
            Log.i("Utils", "getUsageStat: "+s);
            if(s.getPlace().equals(place)){
                return s;
            }
        }
        return null;
    }

    public static String getHourMinuteString(long timeMillis){
            StringBuilder sb = new StringBuilder();
            long hour = TimeUnit.MILLISECONDS.toHours(timeMillis);
            sb.append(hour % 24);
            sb.append(" H  ");
            long minute = TimeUnit.MILLISECONDS.toMinutes(timeMillis) - hour * 60;
            sb.append(minute % 60);
            sb.append(" m  ");
            long seconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis) - minute * 60;
            sb.append(seconds % 60);
            sb.append(" s ");

            return sb.toString();
    }
    public static Geofence createGeofence(String areaName, double latitude, double longitude, float radius){
        return new Geofence.Builder()
                .setCircularRegion(latitude,longitude,radius)
                .setRequestId(areaName)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    public  static String getDate(){
        StringBuilder sb = new StringBuilder();
        Calendar cal = Calendar.getInstance();
        sb.append(cal.get(Calendar.DAY_OF_MONTH));
        sb.append('-');
        sb.append(cal.get(Calendar.MONTH));
        sb.append('-');
        sb.append(cal.get(Calendar.YEAR));
        return sb.toString();
    }





    public static boolean isServiceActive(Context context, Class serviceClass){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices =  activityManager.getRunningServices(10);
        for (ActivityManager.RunningServiceInfo runningService : runningServices) {
           if(runningService.service.getClassName().equals(serviceClass.getName()))
               return  true;
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public static void getCurrentLocationAndCampus(Context context, SharedPrefsManager sp, FutureListener<Pair<Campus, Location>> campusFutureListener){
        LocationServices.getFusedLocationProviderClient(context)
                .getCurrentLocation(new CurrentLocationRequest.Builder()
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .setGranularity(Granularity.GRANULARITY_FINE)
                        .build(),null)
                .addOnSuccessListener(location -> {
                    if(location==null) return;

                    List<Campus> campuses = sp.getCampuses();
                    Log.i(TAG, "getCurrentLocationAndCampus: "+campuses.size());
                    for (Campus c:
                            campuses) {
                        if(c.contains(location)){
                            campusFutureListener.onSuccess(new Pair<Campus,Location>(c,location));
                            return;
                        }
                    }
                    campusFutureListener.onSuccess(new Pair<Campus,Location>(null,location));
                })
                .addOnFailureListener(Throwable::printStackTrace);

    }

    @Nullable
    public static Campus findCampusById(List<Campus> campuses, int hash){
        for (Campus campus : campuses) {
            if(campus.hashCode() == hash)
                return campus;
        }
        return null;
    }
    private static final String TAG = "Utils";

    public static AppsDisplayFragment getFragment(FragmentManager parentFragmentManager, Class appsDisplayFragmentClass) {
        for (Fragment fragment : parentFragmentManager.getFragments()) {
            if(fragment.getClass().getName().equals(appsDisplayFragmentClass.getName())){
                return (AppsDisplayFragment) fragment;
            }
        }
        return null;
    }

    public static boolean isYesterday(long lastUpdatedTime) {
        return lastUpdatedTime < getTodayDayStart();
    }
}
