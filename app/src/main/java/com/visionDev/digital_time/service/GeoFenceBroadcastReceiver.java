package com.visionDev.digital_time.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeoFenceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        GeofencingEvent gfe = GeofencingEvent.fromIntent(intent);

        if(gfe == null || gfe.hasError()){
            return;
        }
        List<Geofence> triggeredGFs = gfe.getTriggeringGeofences();
        switch (gfe.getGeofenceTransition()){
            case Geofence
                    .GEOFENCE_TRANSITION_ENTER :
            {
                Log.i(TAG, "onReceive: Entered "+triggeredGFs.get(0).getRequestId());
                break;
            }
            case Geofence.GEOFENCE_TRANSITION_EXIT:
            {
                Log.i(TAG, "onReceive: Exited"+triggeredGFs.get(0).getRequestId() );
                //load application usage in  exited place
                break;
            }
            default: {
                break;
            }
        }
    }
    public  static String TAG = "GeoFenceListener";
}
