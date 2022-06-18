package com.visionDev.digital_time.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArraySet;
import android.util.Log;

import com.google.firebase.encoders.DataEncoder;
import com.google.firebase.encoders.json.JsonDataEncoderBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.visionDev.digital_time.models.Campus;
import com.visionDev.digital_time.models.UsageStat;
import com.visionDev.digital_time.utils.Constants;
import com.visionDev.digital_time.utils.Utils;

import java.lang.reflect.Type;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

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

    @Nullable
   public UsageStat getUsageStat(String place){
        Gson gson =  new GsonBuilder()
                .create();
        String encode = mSP.getString(place,"{}");
        if(encode.equals("{}")){
            Log.i("PREFS", "getUsageStat: "+"{}");
            return null;
        }
      return   gson.fromJson(encode,UsageStat.class);
    }


    public List<String> getCampusNames(){
        final List<String> campusNames = new ArrayList<>();
        for (UsageStat campus : getUsageStats()) {
            if(!campusNames.contains(campus.getPlace()))
                campusNames.add(campus.getPlace());
        }
        return campusNames;
    }

    public List<UsageStat> getUsageStats(){
        final ArrayList<UsageStat> stats = new ArrayList<>();
        for (String place:
                mSP.getAll().keySet()) {
            if(place.equals(Constants.STAT_LAST_UPDATED))
                continue;

            UsageStat s = getUsageStat(place);
            if(s!=null)
            stats.add(s);
        }
        return stats;
    }

    public void reset() {
        mSP.edit()
                .clear()
                .apply();
    }

    public void setLastUpdated(long currentTimeMillis) {
        mSP
                .edit()
                .putLong(Constants.STAT_LAST_UPDATED,currentTimeMillis)
                .apply();
    }

    public long getLastUpdatedTime(){
        return mSP.getLong(Constants.STAT_LAST_UPDATED, Utils.getTodayDayStart());
    }
}
