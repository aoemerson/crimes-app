package io.github.aoemerson.crimesmvp.model.location;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class GoogleFusedLocationProvider implements CurrentLocationProvider, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private final double defaultLat;
    private final double defaultLng;
    private GoogleApiClient googleApiClient;
    private Runnable runOnConnected;
    private FusedLocationProviderApi fusedLocationApi;

    @Inject
    public GoogleFusedLocationProvider(GoogleApiClient apiClient, FusedLocationProviderApi fusedLocationApi, double defaultLat, double defaultLng) {
        this.defaultLat = defaultLat;
        this.defaultLng = defaultLng;
        this.fusedLocationApi = fusedLocationApi;
        if (apiClient == null) {
            throw new IllegalArgumentException("Google API client has not been initialised - null reference");
        }
        googleApiClient = apiClient;
        googleApiClient.registerConnectionCallbacks(this);
        googleApiClient.registerConnectionFailedListener(this);
    }

    @Override
    public void requestCurrentLocation(final LocationRequestCallback callback) throws MissingPermissionException {
        if (!googleApiClient.isConnected() && googleApiClient.isConnecting()) {
            postponeLocationRequest(callback);
        } else {
            try {
                Location lastLocation = fusedLocationApi
                        .getLastLocation(googleApiClient);
                callback.onLocationObtained(lastLocation.getLatitude(), lastLocation
                        .getLongitude());
            } catch (SecurityException e) {
                throw new MissingPermissionException("Location permission has not been granted", e);
            } catch (NullPointerException e) {
                callback.onLocationRequestError(e);
            }
        }
    }

    @Override
    public void connect() {
        googleApiClient.connect();

//        googleApiClient.
//        googleApiClient.blockingConnect();
    }

    @Override
    public void disconnect() {
        googleApiClient.disconnect();
    }

    @Override
    public void requestDefaultLocation(LocationRequestCallback callback) {
        callback.onLocationObtained(defaultLat, defaultLng);
    }

    void postponeLocationRequest(final LocationRequestCallback callback) {
        runOnConnected = new Runnable() {
            @Override
            public void run() {
                try {
                    if (callback != null)
                        requestCurrentLocation(callback);
                } catch (MissingPermissionException e) {
                    if (callback != null)
                        callback.onLocationRequestError(e);
                }
            }
        };
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.e("Google API Connection failed with result %s", connectionResult.getErrorMessage());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (runOnConnected != null)
            runOnConnected.run();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
