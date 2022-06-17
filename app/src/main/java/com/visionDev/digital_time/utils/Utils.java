package com.visionDev.digital_time.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationServices;
import com.visionDev.digital_time.models.Campus;
import com.visionDev.digital_time.repository.FirestoreManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

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

    public static void getCampuses(FirestoreManager fm,Context context,ListFutureListener<Campus> resultFuture){
        fm.getCampuses(context.getContentResolver(),resultFuture);
    }

    @SuppressLint("MissingPermission")
    public static void getCurrentLocationAndCampus(Context context, FirestoreManager fm, FutureListener<Pair<Campus, Location>> campusFutureListener){
        LocationServices.getFusedLocationProviderClient(context)
                .getCurrentLocation(new CurrentLocationRequest.Builder()

                        .setGranularity(Granularity.GRANULARITY_FINE)
                        .build(),null)
                .addOnSuccessListener(location -> {
                    if(location==null) return;

                    getCampuses(fm,context,new ListFutureListener<Campus>() {
                        @Override
                        public void onSuccess(List<Campus> result) {
                            for (Campus c:
                                    result) {
                                if(c.contains(location)){

                                    campusFutureListener.onSuccess(new Pair<Campus,Location>(c,location));
                                    return;
                                }
                            }
                            campusFutureListener.onSuccess(new Pair<Campus,Location>(null,location));
                        }

                        @Override
                        public void onFailure(Exception e) {
                            campusFutureListener.onFailure(e);
                        }
                    });
                })
                .addOnFailureListener(Throwable::printStackTrace);

    }

}
