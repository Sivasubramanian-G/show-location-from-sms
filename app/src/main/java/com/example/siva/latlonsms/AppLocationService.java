package com.example.siva.latlonsms;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class AppLocationService extends Service implements LocationListener {

    MainActivity ma;
    protected LocationManager locationManager;
    Location location;
    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;

    public AppLocationService(Context context) {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }


    @SuppressLint("MissingPermission")
    public Location getLocation(String provider) {
        Log.d("AppLocationService","gpsLocation function");
        if (locationManager.isProviderEnabled(provider)) {
            location = new Location(provider);
            Log.d("msgas", "before check permission in app");
            locationManager.requestLocationUpdates(provider, MIN_TIME_FOR_UPDATE
                    , MIN_DISTANCE_FOR_UPDATE, this);
            Log.d("msags","location is "+location);
            if(locationManager!=null){
                Log.d("msgas1","locationManager!=null");
                location=locationManager.getLastKnownLocation(provider);
                    if(location!=null){
                        return location;
                    }
                    else{
                        Log.d("AppLocationService","Location is null");
                    }
                }
            }
        else{
            Log.d("AppLocationService","provider is not enabled");
        }
        return null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

