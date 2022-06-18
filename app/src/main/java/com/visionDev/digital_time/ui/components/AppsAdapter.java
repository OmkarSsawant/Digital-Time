package com.visionDev.digital_time.ui.components;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.visionDev.digital_time.databinding.TileAppBinding;
import com.visionDev.digital_time.models.UsageStat;
import com.visionDev.digital_time.utils.Utils;

import java.util.List;
import java.util.Map;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppViewHolder> {


      String[] pkgNames;
    PackageManager pm;
     Map<String,Long> apps;
    public AppsAdapter(Context context) {
        pm =  context.getPackageManager();
    }

    public void setStat(UsageStat s){
        if(s==null) return;
        apps = s.getUsageStats();
        pkgNames =  apps.keySet().toArray(new String[0]);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppViewHolder(TileAppBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {

        try {
            String pkgName = pkgNames[position];
            ApplicationInfo applicationInfo = pm.getApplicationInfo(pkgName,0);
            Drawable appIcon = pm.getApplicationIcon(applicationInfo);
            String appName = pm.getApplicationLabel(applicationInfo).toString();
            Long usedTime = apps.get(pkgName);
            if(usedTime==null) return;
            holder.binding.appIcon.setImageDrawable(appIcon);
            holder.binding.appName.setText(appName);
            holder.binding.usedTime.setText(
                    Utils.getHourMinuteString(usedTime)
            );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return apps == null ? 0 : apps.size();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder{
        TileAppBinding binding;
        public AppViewHolder(TileAppBinding tileAppBinding) {
            super(tileAppBinding.getRoot());
            this.binding = tileAppBinding;
        }
    }
}
