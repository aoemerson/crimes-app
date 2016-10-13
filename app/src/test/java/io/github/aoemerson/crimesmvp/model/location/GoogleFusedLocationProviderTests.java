package io.github.aoemerson.crimesmvp.model.location;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GoogleFusedLocationProviderTests {

    private static final double TEST_LAT = 101.23d;
    private static final double TEST_LNG = 34.5d;
    private static final double DEFAULT_LAT = 52d;
    private static final double DEFAULT_LNG = 0d;


    @Mock GoogleApiClient googleApiClient;
    @Mock FusedLocationProviderApi fusedLocationProviderApi;

    @Mock Location mockedLocation;

    GoogleFusedLocationProvider locationProvider;


    @Before
    public void setup() throws SecurityException {
        locationProvider = new GoogleFusedLocationProvider(googleApiClient, fusedLocationProviderApi, DEFAULT_LAT, DEFAULT_LNG);
        when(fusedLocationProviderApi.getLastLocation(googleApiClient)).thenReturn(mockedLocation);
        when(mockedLocation.getLatitude()).thenReturn(TEST_LAT);
        when(mockedLocation.getLongitude()).thenReturn(TEST_LNG);
        when(googleApiClient.isConnected()).thenReturn(true);
        when(googleApiClient.isConnecting()).thenReturn(false);
    }

    @Test
    public void shouldPostLocationToCallback() throws CurrentLocationProvider.MissingPermissionException, SecurityException {
        CurrentLocationProvider.LocationRequestCallback mockedCallback = Mockito
                .mock(CurrentLocationProvider.LocationRequestCallback.class);

        locationProvider.requestCurrentLocation(mockedCallback);
        verify(mockedCallback, times(1)).onLocationObtained(TEST_LAT, TEST_LNG);

    }

    @Test(expected = CurrentLocationProvider.MissingPermissionException.class)
    public void shouldThrowWrappedSecurityExceptionLocationToCallback() throws SecurityException, CurrentLocationProvider.MissingPermissionException {
        CurrentLocationProvider.LocationRequestCallback mockedCallback = Mockito
                .mock(CurrentLocationProvider.LocationRequestCallback.class);

        when(fusedLocationProviderApi.getLastLocation(googleApiClient))
                .thenThrow(new SecurityException());
        locationProvider.requestCurrentLocation(mockedCallback);
    }

    @Test
    public void shouldPostLocationErrorToCallback() throws CurrentLocationProvider.MissingPermissionException, SecurityException {
        CurrentLocationProvider.LocationRequestCallback mockedCallback = Mockito
                .mock(CurrentLocationProvider.LocationRequestCallback.class);

        NullPointerException nullPointerException = new NullPointerException();
        when(fusedLocationProviderApi.getLastLocation(googleApiClient))
                .thenThrow(nullPointerException);
        locationProvider.requestCurrentLocation(mockedCallback);
        verify(mockedCallback, times(1)).onLocationRequestError(nullPointerException);

    }

    @Test
    public void shouldPostponeOnClientConnecting() throws CurrentLocationProvider.MissingPermissionException {
        when(googleApiClient.isConnected()).thenReturn(false);
        when(googleApiClient.isConnecting()).thenReturn(true);
        CurrentLocationProvider.LocationRequestCallback callback = Mockito
                .mock(CurrentLocationProvider.LocationRequestCallback.class);
        locationProvider.requestCurrentLocation(callback);
        when(googleApiClient.isConnected()).thenReturn(true);
        when(googleApiClient.isConnecting()).thenReturn(false);
        locationProvider.onConnected(null);

        verify(callback, times(1)).onLocationObtained(TEST_LAT, TEST_LNG);
    }

    @Test
    public void shouldConnect() {
        locationProvider.connect();
        verify(googleApiClient, times(1)).connect();
    }


    @Test
    public void shouldDisconnect() {
        locationProvider.disconnect();
        verify(googleApiClient, times(1)).disconnect();
    }

    @Test
    public void shouldProvideDefaultLocation() {
        CurrentLocationProvider.LocationRequestCallback callback = Mockito
                .mock(CurrentLocationProvider.LocationRequestCallback.class);
        locationProvider.requestDefaultLocation(callback);
        verify(callback, times(1)).onLocationObtained(DEFAULT_LAT, DEFAULT_LNG);
    }

}
