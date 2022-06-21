package com.visionDev.digital_time.ui.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;

import com.google.android.material.tabs.TabLayoutMediator;
import com.visionDev.digital_time.MainActivity;
import com.visionDev.digital_time.MainActivityViewModel;
import com.visionDev.digital_time.databinding.FragmentDashboardBinding;
import com.visionDev.digital_time.service.UsageStatsUploadWorker;
import com.visionDev.digital_time.ui.components.DashboardPagerAdapter;
import com.visionDev.digital_time.utils.Utils;

import java.util.Collections;
import java.util.List;


public class DashboardFragment extends Fragment {

    FragmentDashboardBinding binding;
    MainActivityViewModel viewModel;
    DashboardPagerAdapter pagerAdapter;

    private static DashboardFragment fragment;
    public static Fragment get() {
        if(fragment!=null) return  fragment;
        fragment = new DashboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(MainActivityViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStats();
    }

    private void updateStats() {
        OneTimeWorkRequest updateStatsWork = new OneTimeWorkRequest.Builder(UsageStatsUploadWorker.class)
                .addTag("one_time_updater")
                .build();
        WorkManager wm =  WorkManager.getInstance(requireActivity());
        wm.enqueue(updateStatsWork)
                .getState()
                .observe(this,state ->
                {
                    Log.i(TAG, "updateStats: "+state);

                    if(state instanceof Operation.State.SUCCESS){
                        pagerAdapter.setCampusNames(viewModel.getCampusNames());
                        //call load on fragment
                        AppsDisplayFragment appsDisplayFragment = Utils.getFragment(getParentFragmentManager(),AppsDisplayFragment.class);
                        if(appsDisplayFragment!=null){
                            appsDisplayFragment.load();
                        }
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActionBar rb = ((MainActivity) requireActivity())
                .getSupportActionBar();
        if(!rb.isShowing()){
            rb.show();
        }
        pagerAdapter = new DashboardPagerAdapter(requireActivity(),Collections.emptyList());
        binding.campusList.setAdapter(pagerAdapter) ;

         ((MainActivity)requireActivity()).getToolbar();
        new TabLayoutMediator(binding.campusTabs,binding.campusList,(tab,viewPagerPos) -> {
            tab.setText(pagerAdapter.getName(viewPagerPos));
        }).attach();
    }

    private static final String TAG = "DashboardFragment";
}
