package com.visionDev.digital_time.utils;

import android.Manifest;
import android.view.View;

import androidx.core.util.TimeUtils;

import java.sql.Time;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Constants {
    public static final String PLACE_OTHER = "other";
    public static final String CAMPUS_CACHE = "campus_cache";
    public static final String STORE_LOCAL_USAGE_CACHE = "local_cache.usage";
    public static final String STORE_LOCAL_CAMPUS_CACHE = "local_cache.campus";
    public static final long MIN_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(2);
    public static String[] REQUIRED_PERMISSIONS = new String[]{
           Manifest.permission.ACCESS_COARSE_LOCATION,
           Manifest.permission.ACCESS_FINE_LOCATION,
           Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    public static final String USAGE_UPDATER = "usage_updater";
    public static final String STAT_LAST_UPDATED = "usage_updated";
    public  static final int MILLISECONDS_OF_15 = 15 * 60  * 1000;
    public static final int FULLSCREEN =  View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
}
