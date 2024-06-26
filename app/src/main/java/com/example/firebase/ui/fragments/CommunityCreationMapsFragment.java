package com.example.firebase.ui.fragments;


import static com.example.firebase.ui.activitys.MainActivity.MY_PERMISSIONS_REQUEST_LOCATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.firebase.R;
import com.example.firebase.ui.activitys.CommunityCreation;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.function.Supplier;

public class CommunityCreationMapsFragment extends Fragment{

    private static final int REQUEST_CHECK_SETTINGS = 10001;
    private static final String[] GPS_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private Supplier<Double> longitude;
    private Supplier<Double> latitude;
    private LatLng myPos;
    private static GoogleMap googleMap;
    View.OnClickListener onClickListener;
    Circle mapCircle;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            CommunityCreationMapsFragment.googleMap = googleMap;
            GPS(googleMap);
            CircleOptions circleOptions = new CircleOptions();
            // Specifying the center of the circle
            circleOptions.center(new LatLng(0,0));
            // Radius of the circle
            circleOptions.radius(0);
            // Border color of the circle
            circleOptions.strokeColor(Color.BLACK);
            // Fill color of the circle
            circleOptions.fillColor(0x30ff0000);
            // Border width of the circle
            circleOptions.strokeWidth(2);
            // Adding the circle to the GoogleMap
            mapCircle = googleMap.addCircle(circleOptions);
        }
    };
    public CommunityCreationMapsFragment(View.OnClickListener oc){
        this.onClickListener = oc;
    }
    private void GPS(GoogleMap googleMap){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        getGPS(locationRequest,googleMap);
    }
    @SuppressLint("MissingPermission")
    private void getGPS(LocationRequest locationRequest,GoogleMap googleMap){
        if (arePermissionsGranted(GPS_PERMISSIONS) && isGPSEnabled()) {
            LocationServices.getFusedLocationProviderClient(this.getActivity())
                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);

                            LocationServices.getFusedLocationProviderClient(CommunityCreationMapsFragment.this.getActivity())
                                    .removeLocationUpdates(this);
                            if (locationResult!=null &&locationResult.getLocations().size()>0){

                                latitude = () -> locationResult.getLocations().get(locationResult.getLocations().size()-1).getLatitude();
                                longitude = () -> locationResult.getLocations().get(locationResult.getLocations().size()-1).getLongitude();
                            }
                            myPos = new LatLng(latitude.get(),longitude.get());
                            googleMap.addMarker(new MarkerOptions().position(myPos));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(myPos));
                            googleMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
                            toast("GPSlocation: (long,lat) ("+longitude.get()+", "+latitude.get()+")");
                            CommunityCreation.setGPSCords(latitude.get(),longitude.get(),onClickListener);
                        }
                    }, Looper.getMainLooper());


        }else{
            if(!arePermissionsGranted(GPS_PERMISSIONS)){
                toast("need GPS prmition to work");
                ActivityCompat.requestPermissions(this.getActivity(), GPS_PERMISSIONS, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            if(!isGPSEnabled()){
                toast("you need to enable GPS");
                turnOnGPS(locationRequest);
            }
        }
    }


    private void turnOnGPS(LocationRequest locationRequest) {
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this.getContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {

            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                Toast.makeText(getContext(), "GPS is already tured on", Toast.LENGTH_SHORT).show();

            } catch (ApiException e) {

                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Device does not have GPSlocation
                        break;
                }
            }
        });
    }

    private boolean isGPSEnabled(){
        LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
    private boolean arePermissionsGranted(String[] neededPermissions) {
        for (String permission : neededPermissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    private void toast(String text){
        Toast t = new Toast(this.getContext());
        t.setText(text);
        t.show();
    }
    public Circle getMapCircle(){
        return mapCircle;
    }

    public void drawCircle(LatLng point,int radius){    // Instantiating CircleOptions to draw a circle around the marker
        mapCircle.remove();
        CircleOptions circleOptions = new CircleOptions();
        // Specifying the center of the circle
        circleOptions.center(point);
        // Radius of the circle
        circleOptions.radius(radius);
        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);
        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);
        // Border width of the circle
        circleOptions.strokeWidth(2);
        // Adding the circle to the GoogleMap
        mapCircle = googleMap.addCircle(circleOptions);
    }
    public void zoom(int radius){
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myPos));
        float zoomVal = (float) (23 -Math.log(radius)/Math.log(2));
        googleMap.animateCamera( CameraUpdateFactory.zoomTo(zoomVal));
    }
}
