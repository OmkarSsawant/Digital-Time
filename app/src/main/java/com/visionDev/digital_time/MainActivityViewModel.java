package com.visionDev.digital_time;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.core.location.LocationManagerCompat;
import androidx.core.util.Consumer;
import androidx.lifecycle.AndroidViewModel;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.GeoPoint;
import com.visionDev.digital_time.models.Campus;
import com.visionDev.digital_time.repository.FirestoreManager;
import com.visionDev.digital_time.service.UsageStatsUploadWorker;
import com.visionDev.digital_time.utils.Constants;
import com.visionDev.digital_time.utils.FutureListener;
import com.visionDev.digital_time.utils.ListFutureListener;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivityViewModel extends AndroidViewModel {

    private final FirestoreManager firestoreManager;
    private final ExecutorService backgroundWorkers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() +1);

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        firestoreManager = new FirestoreManager(application);
    }



    public void saveCampus(Campus campus, FutureListener<Campus> resultFuture){
        firestoreManager.fetchCampus(campus,getApplication().getContentResolver())
                .addOnSuccessListener(backgroundWorkers,s->{
                    //If that campus is unique
                    if(s.isEmpty())
                    firestoreManager.saveCampus(campus,getApplication().getContentResolver())
                            .addOnSuccessListener(backgroundWorkers,ref->{
                                    ref.get()
                                    .addOnSuccessListener(backgroundWorkers,snap->{
                                        resultFuture.onSuccess(snap.toObject(Campus.class));
                                    })
                                    .addOnFailureListener(backgroundWorkers, resultFuture::onFailure);
                            })
                            .addOnFailureListener(backgroundWorkers, resultFuture::onFailure);
                })
                .addOnFailureListener(backgroundWorkers, resultFuture::onFailure);
    }




    @Override
    protected void onCleared() {
        super.onCleared();
        backgroundWorkers.shutdown();
        try {
            backgroundWorkers.awaitTermination(2000, TimeUnit.MILLISECONDS);
            if(!backgroundWorkers.isShutdown()){
                backgroundWorkers.shutdownNow();
            }
        } catch (InterruptedException e) {
            backgroundWorkers.shutdownNow();
            e.printStackTrace();
        }
    }

    private static final String TAG = "MainActivityViewModel";
}
