package com.visionDev.digital_time.utils;

import com.google.android.gms.location.Geofence;

public class GeofenceUtils {

   public static Geofence createGeofence(String areaName, long latitude, long longitude, float radius){
        return new Geofence.Builder()
                .setCircularRegion(latitude,longitude,radius)
                .setRequestId(areaName)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }
}
