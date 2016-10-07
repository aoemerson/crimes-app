package io.github.aoemerson.crimesmvp.model.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class GoogleFusedLocationProvider implements CurrentLocationProvider {

    private GoogleApiClient googleApiClient;
    Context context;

    public GoogleFusedLocationProvider(GoogleApiClient apiClient) {
        if (!googleApiClient.isConnected()) {
            throw new IllegalArgumentException("Google API client not connected!");
        }
        if (googleApiClient == null) {
            throw new IllegalArgumentException("Google API client has not been initialised - null reference");
        }
        googleApiClient = apiClient;
    }

    @Override
    public void requestCurrentLocation(LocationProvidedCallback callback) {

        try {
            if (ActivityCompat
                    .checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
