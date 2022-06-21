package com.visionDev.digital_time.ui.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.visionDev.digital_time.MainActivityViewModel;
import com.visionDev.digital_time.databinding.FragmentAppListerBinding;
import com.visionDev.digital_time.models.UsageStat;
import com.visionDev.digital_time.repository.SharedPrefsManager;
import com.visionDev.digital_time.ui.components.AppsAdapter;

public class AppsDisplayFragment extends Fragment {

    FragmentAppListerBinding appListerBinding;

    SharedPrefsManager sharedPrefsManager;
    AppsAdapter appsAdapter;
    MainActivityViewModel activityViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefsManager = new SharedPrefsManager(requireContext());
        activityViewModel  = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(MainActivityViewModel.class);
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
         appsAdapter = new AppsAdapter(requireContext());
        rv.setAdapter(appsAdapter);

    }

    public void load(){
        String  place =  requireArguments().getString(STAT_PLACE,"");
        UsageStat stat = activityViewModel.getUsageStatByName(place);
        Log.i(TAG, "load: "+place + " -> "+stat);
        if(stat!=null && appsAdapter!=null && isVisible()) {
            appsAdapter.setStat(stat);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
       load();
    }

    public  static AppsDisplayFragment newFragment(String  place){
        AppsDisplayFragment fragment = new AppsDisplayFragment();
        Bundle args = new Bundle();
        args.putString(STAT_PLACE,place);
        fragment.setArguments(args);
        return fragment;
    }
    public static final String STAT_PLACE="app_used.stat_place";
    private static final String TAG = "AppsDisplayFragment";
}
