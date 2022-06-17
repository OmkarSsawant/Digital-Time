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

    List<UsageStat> statList ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefsManager = new SharedPrefsManager(requireContext());
        statList =  sharedPrefsManager.getUsageStats();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        appListerBinding = FragmentAppListerBinding.inflate(inflater,container,false);
        return  appListerBinding.getRoot();
    }

    public UsageStat loadApps(){
        String place = requireArguments().getString(CAMPUS,"all");
        UsageStat stat = null;
        if(!place.equals("all")){
            for (UsageStat s:
                    statList) {
                Log.i(TAG, "onViewCreated: "+s.getPlace());
                if(s.getPlace().equals(place)){
                    stat = s;
                }
            }
            if(stat==null){
                Toast.makeText(requireContext(),"No such Stat exists",Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
                return null;
            }
        }else{
            //add all
            return null;
        }
        return stat;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated: "+statList.size());
        RecyclerView rv = (RecyclerView) view;
        rv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        AppsAdapter appsAdapter = new AppsAdapter(requireContext());
        rv.setAdapter(appsAdapter);
    }

    public  static AppsDisplayFragment newFragment(String place){
        AppsDisplayFragment fragment = new AppsDisplayFragment();
        Bundle args = new Bundle();
        args.putString(CAMPUS,place);
        fragment.setArguments(args);

        return fragment;
    }
    public static final String CAMPUS="app_used.campus";
    private static final String TAG = "AppsDisplayFragment";
}
