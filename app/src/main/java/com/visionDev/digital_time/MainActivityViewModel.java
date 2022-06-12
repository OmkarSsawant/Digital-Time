package com.visionDev.digital_time;

import android.app.Application;
import android.content.ContentResolver;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.visionDev.digital_time.models.Campus;
import com.visionDev.digital_time.repository.FirestoreManager;

import javax.xml.transform.Result;

public class MainActivityViewModel extends AndroidViewModel {

    final FirestoreManager firestoreManager;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        firestoreManager = new FirestoreManager(application);
    }

    public Task<DocumentReference> saveCampus(Campus campus){
       return firestoreManager.saveCampus(campus,getApplication().getContentResolver());
    }
}
