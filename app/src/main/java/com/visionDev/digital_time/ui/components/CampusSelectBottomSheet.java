package com.visionDev.digital_time.ui.components;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.GeoPoint;
import com.visionDev.digital_time.MainActivityViewModel;
import com.visionDev.digital_time.R;
import com.visionDev.digital_time.databinding.BottomSheetCampusSelectBinding;
import com.visionDev.digital_time.models.Campus;
import com.visionDev.digital_time.ui.screens.CampusSelectorFragment;

import java.util.ArrayList;

public class CampusSelectBottomSheet extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {


    Campus campus;
    BottomSheetCampusSelectBinding selectBinding;
    MainActivityViewModel activityViewModel;
    CampusRadiusListener mCampusListener;
    public static CampusSelectBottomSheet getBottomSheet(Place place){
        CampusSelectBottomSheet fragment= new CampusSelectBottomSheet();
        Bundle args = new Bundle();
        args.putString(CampusSelectorFragment.PLACE_NAME,place.getName().toString());
        if(place.getAddress()!=null)
            args.putString(CampusSelectorFragment.PLACE_ADDRESS,place.getAddress().toString());
        args.putDouble(CampusSelectorFragment.PLACE_LAT,place.getLatLng().latitude);
        args.putDouble(CampusSelectorFragment.PLACE_LON,place.getLatLng().longitude);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheet);
        selectBinding  = BottomSheetCampusSelectBinding.inflate(inflater,container,false);
        campus = new Campus();
        Bundle args = requireArguments();
       String placeName  = args.getString(CampusSelectorFragment.PLACE_NAME);
       String address =  args.getString(CampusSelectorFragment.PLACE_ADDRESS);
        GeoPoint latLng = new GeoPoint(args.getDouble(CampusSelectorFragment.PLACE_LAT,0.0),args.getDouble(CampusSelectorFragment.PLACE_LON,0.0));
        campus.setName(placeName);
        campus.setLocation(latLng);
        campus.setAddress(address);
        return selectBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activityViewModel =  ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(MainActivityViewModel.class);
        selectBinding.placeNameLabel.setText("Campus Name");
        selectBinding.inCampusName.setText(campus.getName());
        selectBinding.address.setText(campus.getAddress());
        selectBinding.seekBar.setOnSeekBarChangeListener(this);
        selectBinding.button.setOnClickListener(v->{
            campus.setRange(radius);
        activityViewModel.saveCampus(campus)
                .addOnSuccessListener(requireActivity(),success -> {
                    requireActivity().getSupportFragmentManager()
                            .popBackStack();
                    Toast.makeText(requireContext(),"Saved Campus  Successfully",Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(requireActivity(),fail->{
                    requireActivity().getSupportFragmentManager()
                            .popBackStack();
                    Toast.makeText(requireContext(),"Failed to save Campus",Toast.LENGTH_SHORT).show();
                });
        });
    }


   public void setCampusListener(CampusRadiusListener campusListener){
        this.mCampusListener = campusListener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        ((View)seekBar.getParent().getParent())
                .setBackgroundColor(Color.TRANSPARENT);

    }

    float radius;
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "onStopTrackingTouch: "+seekBar.getProgress());
        ((View)seekBar.getParent().getParent())
                .setBackgroundColor(Color.WHITE);
        if(mCampusListener!=null){
            radius = seekBar.getProgress();
            mCampusListener.onCampusRadiusChange(seekBar.getProgress());
        }else{
            Log.i(TAG, "onStopTrackingTouch: Campus Listener is NULL");
        }
    }




    private static final String TAG = "CampusSelectBottomSheet";



   public interface  CampusRadiusListener{
        void onCampusRadiusChange(float radius);
    }
}
