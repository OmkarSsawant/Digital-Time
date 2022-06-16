package com.visionDev.digital_time.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.encoders.DataEncoder;
import com.google.firebase.encoders.json.JsonDataEncoderBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.visionDev.digital_time.models.UsageStat;

import java.lang.reflect.Type;
import java.sql.Types;

public class SharedPrefsManager {
    private static final String STORE = "com.visionDev.local_cache";
    private SharedPreferences mSP ;
    public SharedPrefsManager(Context context){
        mSP = context.getSharedPreferences(STORE,Context.MODE_PRIVATE);
    }

  public   void saveUsageStat(UsageStat stat){
        Gson gson =  new GsonBuilder()
                .create();
        String jsonEncoded = gson.toJson(stat);
        mSP.edit()
                .putString(stat.getPlace(),jsonEncoded)
                .apply();
    }

   public UsageStat getUsageStat(String place){
        Gson gson =  new GsonBuilder()
                .create();
      return   gson.fromJson(mSP.getString(place,"{}"),UsageStat.class);
    }
}
