package com.gtappdevelopers.bankrehovot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationReceiver extends BroadcastReceiver {
    public static final String COUNTRY_EXTRA = "country_extra";

    @Override
    public void onReceive(Context context, Intent intent) {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String country = addresses.get(0).getCountryName();

            Intent activityIntent = new Intent(context, MyProfile.class);
            activityIntent.putExtra(COUNTRY_EXTRA, country);
            context.startActivity(activityIntent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}