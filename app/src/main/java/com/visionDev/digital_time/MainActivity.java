package com.visionDev.digital_time;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.location.Geofence;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.firestore.FirebaseFirestore;
import com.visionDev.digital_time.models.Campus;
import com.visionDev.digital_time.repository.FirestoreManager;
import com.visionDev.digital_time.service.PlaceTrackerService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainActivityViewModel.class);

       ActivityResultLauncher<String[]> permissionAsker = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), results->{
            if (results.containsValue(false)){
                Toast.makeText(this,"Please Grant  Permissions",Toast.LENGTH_SHORT).show();
                finish();
            }
        });


       permissionAsker.launch(new String[]{
               Manifest.permission.ACCESS_COARSE_LOCATION,
               Manifest.permission.ACCESS_FINE_LOCATION,
               Manifest.permission.READ_EXTERNAL_STORAGE,
       });



        if(!Places.isInitialized()){
            Places.initialize(this,BuildConfig.MAPS_API_KEY);
        }

        viewModel.updatesStats();
        final FirestoreManager firestore = new FirestoreManager(this);
        List<Geofence> geofences = new ArrayList<>();
        for (Campus c :
                firestore.getCampuses(getContentResolver())) {
            geofences.add(c.toGeofence());
            Log.i(TAG, "onCreate: "+c.toGeofence());
        }

        startDigitalTimeService(geofences);



    }


    void startDigitalTimeService(List<Geofence> geofences){


        Intent digitalService = new Intent(this, PlaceTrackerService.class);
        digitalService.putParcelableArrayListExtra(PlaceTrackerService.OBSERVED_AREAS,new ArrayList(geofences));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(digitalService);
        }else{
            startService(digitalService);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.updatesStats();
    }

    private static final String TAG = "MainActivity";
}