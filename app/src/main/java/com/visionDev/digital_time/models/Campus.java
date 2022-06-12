package com.visionDev.digital_time.models;

import com.google.android.gms.location.Geofence;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.type.LatLng;
import com.visionDev.digital_time.utils.Utils;

import java.io.Serializable;


public class Campus implements Serializable {
    String name;
    float range;
    GeoPoint location;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    String address;
    public String getName() {
        return name;
    }

    public float getRange() {
        return range;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public static Campus fromSnapshot(DocumentSnapshot ds){
        return ds.toObject(Campus.class);
    }

    public Geofence toGeofence(){
        return Utils.createGeofence(name,location.getLatitude(),location.getLongitude(),range);
    }
}
