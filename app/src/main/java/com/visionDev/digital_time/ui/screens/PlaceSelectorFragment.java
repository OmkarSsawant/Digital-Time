package com.visionDev.digital_time.ui.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.visionDev.digital_time.R;
import com.visionDev.digital_time.databinding.FragmentCreateCampusBinding;

public class PlaceSelectorFragment extends Fragment {

    FragmentCreateCampusBinding createCampusBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createCampusBinding = FragmentCreateCampusBinding.inflate(inflater,container,false);
        return  createCampusBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
