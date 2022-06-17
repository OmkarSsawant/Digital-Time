package com.visionDev.digital_time.ui.components;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.visionDev.digital_time.ui.screens.AppsDisplayFragment;

import java.util.List;

public class DashboardPagerAdapter extends FragmentStateAdapter {

    List<String> mCampusNames;
    public DashboardPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<String> campusNames) {
        super(fragmentActivity);
        mCampusNames = campusNames;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return AppsDisplayFragment.newFragment(mCampusNames.get(position));
    }


    @Override
    public int getItemCount() {
        return mCampusNames.size();
    }
}
