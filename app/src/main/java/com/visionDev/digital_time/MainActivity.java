package com.visionDev.digital_time;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;

import com.google.android.gms.location.Geofence;
import com.google.firebase.firestore.FirebaseFirestore;
import com.visionDev.digital_time.models.Campus;
import com.visionDev.digital_time.repository.FirestoreManager;
import com.visionDev.digital_time.service.PlaceTrackerService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirestoreManager firestore = new FirestoreManager(this);
        List<Geofence> geofences = new ArrayList<>();
        for (Campus c :
                firestore.getCampuses(getContentResolver())) {
            geofences.add(c.toGeofence());
        }

        startDigitalTimeService(geofences);

    }


    void startDigitalTimeService(List<Geofence> geofences){
        Intent digitalService = new Intent(this, PlaceTrackerService.class);
        digitalService.putParcelableArrayListExtra(PlaceTrackerService.OBSERVED_AREAS, (ArrayList<? extends Parcelable>) geofences);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(digitalService);
        }else{
            startService(digitalService);
        }
    }
}