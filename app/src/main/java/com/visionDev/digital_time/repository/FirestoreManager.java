package com.visionDev.digital_time.repository;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.FirestoreClient;
import com.visionDev.digital_time.models.IntervalUsageStat;
import com.visionDev.digital_time.utils.Utils;

import java.util.Map;

public class FirestoreManager {

   private static FirebaseApp APP;


    public FirestoreManager(Context ctx){
       if(FirebaseApp.getApps(ctx).isEmpty()){
           APP = FirebaseApp.initializeApp(ctx);
       }
       else{
           APP = FirebaseApp.getApps(ctx).get(0);
       }

    }


   public Task<DocumentReference> saveStat(IntervalUsageStat stat, ContentResolver cr){
      FirebaseFirestore firestore =   FirebaseFirestore.getInstance(APP);
      @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(cr, Settings.Secure.ANDROID_ID);
      String path = deviceId +'/'+ Utils.getDate() +'/' + stat.getPlace();
      return firestore.collection(path)
              .add(stat);
    }

}
