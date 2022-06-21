package com.visionDev.digital_time.ui.components;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.visionDev.digital_time.MainActivityViewModel;
import com.visionDev.digital_time.models.Campus;
import com.visionDev.digital_time.models.UsageStat;
import com.visionDev.digital_time.repository.SharedPrefsManager;
import com.visionDev.digital_time.ui.screens.AppsDisplayFragment;
import com.visionDev.digital_time.utils.Utils;

import java.util.List;

public class DashboardPagerAdapter extends FragmentStateAdapter {

    List<String> mCampusNames;
    MainActivityViewModel viewModel;
    public DashboardPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<String> campusNames) {
        super(fragmentActivity);
        mCampusNames = campusNames;
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(fragmentActivity.getApplication())
                .create(MainActivityViewModel.class);

    }

   public void setCampusNames(List<String> campusNames ){
       mCampusNames = campusNames;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return AppsDisplayFragment.newFragment(
                mCampusNames.get(position)
        );
    }


    @Override
    public int getItemCount() {
        return mCampusNames.size();
    }

    public String getName(int viewPagerPos) {
        return mCampusNames.get(viewPagerPos);
    }
}
