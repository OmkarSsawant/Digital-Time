package com.visionDev.digital_time.repository;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.visionDev.digital_time.models.Campus;
import com.visionDev.digital_time.models.UsageStat;
import com.visionDev.digital_time.utils.ListFutureListener;
import com.visionDev.digital_time.utils.Utils;

import java.util.ArrayList;
import java.util.List;

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


   public Task<Void> saveStat(UsageStat stat, ContentResolver cr){
      FirebaseFirestore firestore =   FirebaseFirestore.getInstance(APP);
      @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(cr, Settings.Secure.ANDROID_ID);
      return firestore.collection(deviceId)
            .document("stats")
              .collection(Utils.getDate())
              .document(stat.getPlace())
              .set(stat);

   }


    public Task<DocumentReference> saveCampus(Campus campus, ContentResolver cr){
        FirebaseFirestore firestore =   FirebaseFirestore.getInstance(APP);
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(cr, Settings.Secure.ANDROID_ID);
        return firestore.collection(deviceId)
                .document("campuses")
                .collection("places")
                .add(campus);
    }

    public void getCampuses(ContentResolver cr, ListFutureListener<Campus> futureListener){
        ArrayList<Campus> campuses = new ArrayList<>();
        FirebaseFirestore firestore =   FirebaseFirestore.getInstance(APP);
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(cr, Settings.Secure.ANDROID_ID);
        firestore.collection(deviceId)
                .document("campuses")
                .collection("places")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                   List<DocumentSnapshot> snapshots =  queryDocumentSnapshots.getDocuments();
                    for (int i = 0; i < snapshots.size(); i++) {
                       DocumentSnapshot ds =  snapshots.get(i);
                        campuses.add(Campus.fromSnapshot(ds));
                    }
                    futureListener.onSuccess(campuses);
                })
                .addOnFailureListener(futureListener::onFailure);
    }


    public Task<QuerySnapshot> fetchCampus(Campus campus, ContentResolver cr){
        FirebaseFirestore firestore =   FirebaseFirestore.getInstance(APP);
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(cr, Settings.Secure.ANDROID_ID);
        return  firestore.collection(deviceId)
                .document("campuses")
                .collection("places")
                .whereEqualTo("location",campus.getLocation())
                .whereEqualTo("name",campus.getName())
                .get();

    }
}
