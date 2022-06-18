package com.visionDev.digital_time.ui.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.visionDev.digital_time.databinding.FragmentAppListerBinding;
import com.visionDev.digital_time.models.UsageStat;
import com.visionDev.digital_time.repository.SharedPrefsManager;
import com.visionDev.digital_time.ui.components.AppsAdapter;

import java.util.List;

public class AppsDisplayFragment extends Fragment {

    FragmentAppListerBinding appListerBinding;

    SharedPrefsManager sharedPrefsManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefsManager = new SharedPrefsManager(requireContext());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        appListerBinding = FragmentAppListerBinding.inflate(inflater,container,false);
        return  appListerBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rv = appListerBinding.appList;
        rv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        AppsAdapter appsAdapter = new AppsAdapter(requireContext());
        rv.setAdapter(appsAdapter);
        UsageStat stat = (UsageStat) requireArguments().getSerializable(STAT);
        if(stat!=null){
            appsAdapter.setStat(stat);
            appListerBinding.category.setText(stat.toString());
        }else{
            Toast.makeText(view.getContext(),"Stat is null",Toast.LENGTH_SHORT).show();
        }

    }


    public  static AppsDisplayFragment newFragment(UsageStat stat){
        AppsDisplayFragment fragment = new AppsDisplayFragment();
        Bundle args = new Bundle();
        args.putSerializable(STAT,stat);
        fragment.setArguments(args);

        return fragment;
    }
    public static final String STAT="app_used.stat";
    private static final String TAG = "AppsDisplayFragment";
}
