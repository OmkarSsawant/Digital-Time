package com.visionDev.digital_time.ui.screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsetsController;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.visionDev.digital_time.MainActivity;
import com.visionDev.digital_time.R;
import com.visionDev.digital_time.ui.components.CampusSelectBottomSheet;
import com.visionDev.digital_time.utils.Constants;

import java.lang.invoke.ConstantCallSite;
import java.util.Arrays;
import java.util.Objects;

public class CampusSelectorFragment extends Fragment implements PlaceSelectionListener, CampusSelectBottomSheet.CampusRadiusListener, GoogleMap.OnMarkerClickListener {

    GoogleMap mGoogleMap;
    Place mSelectedCampus;
    Circle circle;
    CampusSelectBottomSheet selectBottomSheet ;
    private final OnMapReadyCallback callback = googleMap -> {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.setOnMarkerClickListener(this);
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
            if (container != null) {
                container.getRootView().setSystemUiVisibility(Constants.FULLSCREEN);
            }
        container.getRootView().setOnSystemUiVisibilityChangeListener(systemUiFlag -> {
            if(systemUiFlag!=   Constants.FULLSCREEN){
                container.getRootView().setSystemUiVisibility(Constants.FULLSCREEN);
            }
        });
        return inflater.inflate(R.layout.fragment_campus_selector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
//            mapFragment.getMapAsync(callback);
        }

        AutocompleteSupportFragment mapSearchFragment =
                (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        if(mapSearchFragment != null){
            mapSearchFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
            mapSearchFragment.setOnPlaceSelectedListener(this);

        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireView()
        .getRootView().setSystemUiVisibility(0);
    }

    @Override
    public void onPlaceSelected(@NonNull Place place) {
        if(mGoogleMap==null || place.getLatLng() == null) return;
        mSelectedCampus = place;
        mGoogleMap.addMarker(new MarkerOptions().position(Objects.requireNonNull(mSelectedCampus.getLatLng())).title("Marker of "+place.getName()));



        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),17f),5000,null);
        Toast.makeText(requireContext(),"onPlaceSelected: "+place.getLatLng(),Toast.LENGTH_SHORT).show();

        selectBottomSheet = CampusSelectBottomSheet.getBottomSheet(mSelectedCampus);
        selectBottomSheet.setCampusListener(this);
        selectBottomSheet
                .show(getParentFragmentManager(),"campus_sheet");
        circle = mGoogleMap.addCircle(
                new CircleOptions()
        .center(mSelectedCampus.getLatLng())
        .radius(10)
                .strokeColor(Color.BLUE)
                .fillColor(Color.argb(50,135,206,235))

        );


    }

    @Override
    public void onError(@NonNull Status status) {

    }

    private static final String TAG = "CampusSelectorFragment";
    public static final String PLACE_NAME = "place_name";
    public static final String PLACE_ADDRESS = "place_address";
    public static final String PLACE_LAT = "place_latitude";
    public static final String PLACE_LON = "place_longitude";


    @Override
    public void onCampusRadiusChange(float radius) {
        if(mGoogleMap!=null && mSelectedCampus!=null && mSelectedCampus.getLatLng()!=null){
           circle.remove();
            circle = mGoogleMap.addCircle(
                    new CircleOptions()
                            .center(mSelectedCampus.getLatLng())
                            .radius(radius)
                            .strokeColor(Color.BLUE)
                            .fillColor(Color.argb(50,135,206,235))

            );

        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if(selectBottomSheet==null)
            selectBottomSheet = CampusSelectBottomSheet.getBottomSheet(mSelectedCampus);
        selectBottomSheet.setCampusListener(this);
        selectBottomSheet
                .show(getParentFragmentManager(),"campus_sheet");
        return false;
    }
}