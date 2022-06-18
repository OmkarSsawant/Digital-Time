package com.visionDev.digital_time.ui.components;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.visionDev.digital_time.models.UsageStat;
import com.visionDev.digital_time.repository.SharedPrefsManager;
import com.visionDev.digital_time.ui.screens.AppsDisplayFragment;
import com.visionDev.digital_time.utils.Utils;

import java.util.List;

public class DashboardPagerAdapter extends FragmentStateAdapter {

    List<String> mCampusNames;
    List<UsageStat> statList ;
    public DashboardPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<String> campusNames) {
        super(fragmentActivity);
        mCampusNames = campusNames;

        statList =  (new SharedPrefsManager(fragmentActivity)).getUsageStats();

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return AppsDisplayFragment.newFragment(
                Utils.getUsageStat(mCampusNames.get(position),statList)
        );
    }


    @Override
    public int getItemCount() {
        return mCampusNames.size();
    }
}
