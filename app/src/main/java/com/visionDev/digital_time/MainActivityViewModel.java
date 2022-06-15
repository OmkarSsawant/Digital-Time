package com.visionDev.digital_time;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.visionDev.digital_time.models.Campus;
import com.visionDev.digital_time.models.IntervalUsageStat;
import com.visionDev.digital_time.repository.FirestoreManager;
import com.visionDev.digital_time.service.UsageStatsUploadWorker;
import com.visionDev.digital_time.utils.FutureListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivityViewModel extends AndroidViewModel {

    private final FirestoreManager firestoreManager;
    private final ExecutorService backgroundWorkers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() +1);
    private SharedPreferences sharedPreferences ;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        firestoreManager = new FirestoreManager(application);
        sharedPreferences = application.getSharedPreferences("current.campus", Context.MODE_PRIVATE);
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

    /*
    * All the Stats are made uploaded to firestore
    * */
    public void updatesStats() {
       String placeName =  sharedPreferences.getString("placeName","UNKNOWN");
        Data input = UsageStatsUploadWorker.buildData(null,null,placeName);
        OneTimeWorkRequest updateStatsReq = new  OneTimeWorkRequest.Builder(UsageStatsUploadWorker.class)
                .addTag("UPDATE_STATS")
                .setInputData(input)
                .build();
        WorkManager.getInstance(getApplication())
                .enqueue(updateStatsReq);

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
