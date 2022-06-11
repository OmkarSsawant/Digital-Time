package com.visionDev.digital_time.models;

import com.google.android.gms.location.Geofence;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.type.LatLng;
import com.visionDev.digital_time.utils.Utils;

public class Campus {
    String name;
    float range;
    GeoPoint location;


    public static Campus fromSnapshot(DocumentSnapshot ds){
        return ds.toObject(Campus.class);
    }

    public Geofence toGeofence(){
        return Utils.createGeofence(name,location.getLatitude(),location.getLongitude(),range);
    }
}
