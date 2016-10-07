package io.github.aoemerson.crimesmvp.model.location;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class GoogleFusedLocationProvider implements CurrentLocationProvider {

    private GoogleApiClient googleApiClient;

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
    public void requestCurrentLocation(@NonNull LocationRequestCallback callback) throws MissingPermissionException {
        try {
            Location lastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(googleApiClient);
                callback.onLocationObtained(lastLocation.getLatitude(), lastLocation.getLongitude());
        } catch (SecurityException e) {
            throw new MissingPermissionException("Location permission has not been granted", e);
        }
        catch (NullPointerException e) {
            callback.onLocationRequestError(e);
        }
    }

}
