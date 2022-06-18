package com.visionDev.digital_time;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AppOpsManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;
import com.google.android.material.appbar.MaterialToolbar;
import com.visionDev.digital_time.databinding.ActivityMainBinding;
import com.visionDev.digital_time.service.PlaceTrackerService;
import com.visionDev.digital_time.ui.screens.CampusSelectorFragment;
import com.visionDev.digital_time.utils.Constants;
import com.visionDev.digital_time.utils.Utils;

public class MainActivity extends AppCompatActivity {

    MainActivityViewModel viewModel;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.mToolbar);
        ActionBar actionBar  = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
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



        binding.drawerContent
                .addDrawerListener(new ActionBarDrawerToggle(
                        this,
                        binding.drawerContent,
                        binding.mToolbar,
                        R.string.open_drawer,
                        R.string.close_drawer
                ));
        binding.drawerNavView.setNavigationItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.add_campus:{
                    getSupportFragmentManager()
                            .beginTransaction()
                            .addToBackStack(CampusSelectorFragment.class.getName())
                            .replace(R.id.host,CampusSelectorFragment.get());
                    break;
                }
                case R.id.show_campuses:{
                    break;
                }
            }
            return false;
        });

        requireAppService();
    }

    private void requireAppService() {
        //Check if service is not active than start it
        if(!Utils.isServiceActive(this, PlaceTrackerService.class)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this,PlaceTrackerService.class));
            }else{
                startService(new Intent(this,PlaceTrackerService.class));
            }
        }
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



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private static final String TAG = "MainActivity";

    public MaterialToolbar getToolbar() {
        return binding.mToolbar;
    }
}