package com.visionDev.digital_time.utils;

import com.google.android.gms.location.Geofence;

import java.util.Calendar;
import java.util.Date;

public class Utils {
    public  static long getTodayDayStart(){
       Date today=  Calendar.getInstance().getTime();
             Calendar calendar =   Calendar.getInstance();
                            calendar.setTime(today);
                            calendar.set(Calendar.HOUR_OF_DAY,0);
                            calendar.set(Calendar.MINUTE,0);
                            calendar.set(Calendar.SECOND,0);
      return  calendar.getTime().getTime();
    }

    public static Geofence createGeofence(String areaName, long latitude, long longitude, float radius){
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
}
