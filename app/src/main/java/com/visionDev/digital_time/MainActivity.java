package com.visionDev.digital_time;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.OneTimeWorkRequest;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.libraries.places.api.Places;
import com.visionDev.digital_time.models.Campus;
import com.visionDev.digital_time.repository.FirestoreManager;
import com.visionDev.digital_time.service.PlaceTrackerService;
import com.visionDev.digital_time.service.UsageStatsUploadWorker;
import com.visionDev.digital_time.utils.Constants;
import com.visionDev.digital_time.utils.ListFutureListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

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


       permissionAsker.launch(Constants.REQUIRED_PERMISSIONS);



       if(!hasUsageStatsSystemPermission()){
            new AlertDialog.Builder(this)
                   .setTitle("Permission Required")
                   .setMessage("Usage Stats permission is required to display you data of your application usage \n Please Grant Permission")
                    .setPositiveButton("Grant", (dialogInterface, i) -> {
                        askUsageStatsPermission();
                    })
                   .setNegativeButton("Deny",((dialogInterface, i) -> {
                       dialogInterface.dismiss();
                       finish();
                   }))
                   .setCancelable(false)
                   .show();

       }


        if(!Places.isInitialized()){
            Places.initialize(this,BuildConfig.MAPS_API_KEY);
        }





        scheduleDayUpdater();
    }

    private void scheduleDayUpdater() {
       AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.HOUR_OF_DAY,11);
//        c.set(Calendar.MINUTE,50);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30),AlarmManager.INTERVAL_DAY,getPendingDigitalTimeService());
    }

    private boolean hasUsageStatsSystemPermission() {
        AppOpsManager appOpsManager = (AppOpsManager) getSystemService(APP_OPS_SERVICE);
       int mode =  appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(),getPackageName());
        if (mode == AppOpsManager.MODE_DEFAULT) {
            return  (checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            return  (mode == AppOpsManager.MODE_ALLOWED);
        }
    }

    private void askUsageStatsPermission() {
        Intent systemPermission = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(systemPermission);
    }


    PendingIntent getPendingDigitalTimeService(){
        Intent digitalService = new Intent(MainActivity.this, PlaceTrackerService.class);
        return PendingIntent.getService(this,0,digitalService,PendingIntent.FLAG_CANCEL_CURRENT);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private static final String TAG = "MainActivity";
}