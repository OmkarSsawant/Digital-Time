package com.visionDev.digital_time.models;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
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

    public boolean contains(Location curLoc){
        float[] res = new float[1];
        Location.distanceBetween(curLoc.getLatitude(),
                curLoc.getLongitude(),
                location.getLatitude(),
                location.getLongitude(),
                res);
        float distance = res[0];
        Log.d("CAMPUS", getName() + " with location "+ location + " contain " + curLoc + " distance is " + distance + ((distance<=range) ? "CONTAINS" : "DOES'T CONTAIN"));
        return distance < getRange();
    }
    public Geofence toGeofence(){
        return Utils.createGeofence(name,location.getLatitude(),location.getLongitude(),range);
    }


    public LatLng toLocation() {
        return new LatLng(getLocation().getLatitude(),getLocation().getLongitude());
    }
}
