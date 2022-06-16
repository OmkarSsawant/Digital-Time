package com.visionDev.digital_time.utils;

import android.Manifest;

import androidx.core.util.TimeUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Constants {
    public static final String PLACE_OTHER = "other";
    public static String[] REQUIRED_PERMISSIONS = new String[]{
           Manifest.permission.ACCESS_COARSE_LOCATION,
           Manifest.permission.ACCESS_FINE_LOCATION,
           Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    public static final String USAGE_UPDATER = "usage.updater";
    public  static final int MILLISECONDS_OF_15 = 15 * 60  * 1000;
}
