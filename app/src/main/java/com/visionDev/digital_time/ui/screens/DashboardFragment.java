package com.visionDev.digital_time.ui.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayoutMediator;
import com.visionDev.digital_time.MainActivity;
import com.visionDev.digital_time.MainActivityViewModel;
import com.visionDev.digital_time.databinding.FragmentDashboardBinding;
import com.visionDev.digital_time.repository.SharedPrefsManager;
import com.visionDev.digital_time.ui.components.DashboardPagerAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class DashboardFragment extends Fragment {

    FragmentDashboardBinding binding;
    MainActivityViewModel viewModel;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActionBar rb = ((MainActivity) requireActivity())
                .getSupportActionBar();
        if(!rb.isShowing()){
            rb.show();
        }
        SharedPrefsManager sp = new SharedPrefsManager(view.getContext());
        List<String> campuses =  sp.getCampusNames();
        Log.i(TAG, "onViewCreated: "+campuses);
        DashboardPagerAdapter pagerAdapter = new DashboardPagerAdapter(requireActivity(),campuses);
        binding.campusList.setAdapter(pagerAdapter) ;
         ((MainActivity)requireActivity()).getToolbar();
        new TabLayoutMediator(binding.campusTabs,binding.campusList,(tab,viewPagerPos) -> {
            tab.setText(campuses.get(viewPagerPos));
        }).attach();
    }

    private static final String TAG = "DashboardFragment";
}
