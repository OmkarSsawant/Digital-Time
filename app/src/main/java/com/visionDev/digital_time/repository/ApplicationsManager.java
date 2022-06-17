package com.visionDev.digital_time.repository;

import android.app.appsearch.AppSearchManager;
import android.content.Context;
import android.content.pm.PackageManager;

public class ApplicationsManager {
    AppSearchManager appSearchManager;
    public ApplicationsManager(Context context){
    PackageManager packageManager =  context.getPackageManager();
    }
}
