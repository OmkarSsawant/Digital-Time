package com.visionDev.digital_time.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.visionDev.digital_time.models.Campus;
import com.visionDev.digital_time.models.UsageStat;
import com.visionDev.digital_time.utils.Constants;
import com.visionDev.digital_time.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

public class SharedPrefsManager {

    private final SharedPreferences mUsageSP;
    private final SharedPreferences mCampusSP;
    Gson usageStatGson =  new GsonBuilder()
            .create();
    Gson campusGson =  new GsonBuilder()
            .create();
    public SharedPrefsManager(Context context){
        mUsageSP = context.getSharedPreferences(Constants.STORE_LOCAL_USAGE_CACHE,Context.MODE_PRIVATE);
        mCampusSP = context.getSharedPreferences(Constants.STORE_LOCAL_CAMPUS_CACHE,Context.MODE_PRIVATE);
    }

  public   void saveUsageStat(UsageStat stat){

        String jsonEncoded = usageStatGson.toJson(stat);
        mUsageSP.edit()
                .putString(stat.getPlace(),jsonEncoded)
                .apply();
    }

    @Nullable
   public UsageStat getUsageStat(String place){

        String encode = mUsageSP.getString(place,"{}");
        if(encode.equals("{}") ){
            Log.i("PREFS", "getUsageStat: "+"{}");
            return null;
        }
      return   usageStatGson.fromJson(encode,UsageStat.class);
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
                mUsageSP.getAll().keySet()) {
            if(place.equals(Constants.STAT_LAST_UPDATED))
                continue;

            UsageStat s = getUsageStat(place);
            if(s!=null)
            stats.add(s);
        }
        return stats;
    }

    public void reset() {
        mUsageSP.edit()
                .clear()
                .apply();
    }

    public void setLastUpdated(long currentTimeMillis) {
        mUsageSP
                .edit()
                .putLong(Constants.STAT_LAST_UPDATED,currentTimeMillis)
                .apply();
    }

    public long getLastUpdatedTime(){
        return mUsageSP.getLong(Constants.STAT_LAST_UPDATED, Utils.getTodayDayStart());
    }

    public List<Campus> getCampuses(){
        List<Campus> campuses  = new ArrayList<>();
        for (String key:
             mCampusSP.getAll().keySet()) {
            Log.i(TAG, "getCampuses: "+mCampusSP.getString(key,"{}"));
                Campus c = campusGson.fromJson(mCampusSP.getString(key,"{}"),Campus.class);
                campuses.add(c);
            }
        return  campuses;
    }

    public  void saveCampuses(List<Campus> campus){
        if(campus.isEmpty())
            mCampusSP.edit()
            .clear()
            .apply();
        SharedPreferences.Editor editor =         mCampusSP.edit();
        for (Campus c : campus){
            String encoded = campusGson.toJson(c,Campus.class);
                 editor.putString(c.getName(),encoded);
        }
                editor.apply();
    }

    private static final String TAG = "SharedPrefsManager";

}
