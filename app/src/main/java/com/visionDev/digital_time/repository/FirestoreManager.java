package com.visionDev.digital_time.repository;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.FirestoreClient;
import com.visionDev.digital_time.models.Campus;
import com.visionDev.digital_time.models.IntervalUsageStat;
import com.visionDev.digital_time.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirestoreManager {

   private static FirebaseApp APP;


    public FirestoreManager(Context ctx){
        if(APP!=null) return;
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


    public Task<DocumentReference> saveCampus(Campus campus, ContentResolver cr){
        FirebaseFirestore firestore =   FirebaseFirestore.getInstance(APP);
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(cr, Settings.Secure.ANDROID_ID);
        return firestore.collection(deviceId)
                .document()
                .collection("campuses")
                .add(campus);
    }

    public List<Campus> getCampuses(ContentResolver cr){
        ArrayList<Campus> campuses = new ArrayList<>();
        FirebaseFirestore firestore =   FirebaseFirestore.getInstance(APP);
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(cr, Settings.Secure.ANDROID_ID);
        firestore.collection(deviceId)
                .document()
                .collection("campuses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                   List<DocumentSnapshot> snapshots =  queryDocumentSnapshots.getDocuments();
                    for (int i = 0; i < snapshots.size(); i++) {
                       DocumentSnapshot ds =  snapshots.get(i);
                        campuses.add(Campus.fromSnapshot(ds));
                    }
                })
                .addOnFailureListener(e -> {});
        return campuses;
    }

}
