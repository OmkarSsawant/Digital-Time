package com.visionDev.digital_time.utils;

import android.Manifest;

import java.util.List;

public class Constants {
   public static String[] REQUIRED_PERMISSIONS = new String[]{
           Manifest.permission.ACCESS_COARSE_LOCATION,
           Manifest.permission.ACCESS_FINE_LOCATION,
           Manifest.permission.READ_EXTERNAL_STORAGE,
    };
}
