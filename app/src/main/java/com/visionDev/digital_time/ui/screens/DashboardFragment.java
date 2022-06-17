package com.visionDev.digital_time.ui.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;
import com.visionDev.digital_time.databinding.FragmentDashboardBinding;
import com.visionDev.digital_time.ui.components.DashboardPagerAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class DashboardFragment extends Fragment {

    FragmentDashboardBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<String> campuses =  Arrays.asList("Om Nagar","other");//TODO:fetch from firebase
        DashboardPagerAdapter pagerAdapter = new DashboardPagerAdapter(requireActivity(),campuses);
        binding.campusList.setAdapter(pagerAdapter) ;
        new TabLayoutMediator(binding.campusTabs,binding.campusList,(tab,viewPagerPos) -> {
            tab.setText(campuses.get(viewPagerPos));

        }).attach();
    }
}
