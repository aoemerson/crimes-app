package io.github.aoemerson.crimesmvp.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.aoemerson.crimesmvp.model.PoliceClient;
import io.github.aoemerson.crimesmvp.model.data.Crime;
import io.github.aoemerson.crimesmvp.model.location.CurrentLocationProvider;
import io.github.aoemerson.crimesmvp.view.CrimesView;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CrimeListPresenterTests {

    @Mock
    CrimesView crimesView;

    @Mock
    PoliceClient policeClient;

    @Mock
    CurrentLocationProvider locationProvider;

    private CrimeListPresenterImpl crimesPresenter;

    @Before
    public void setup() {
        crimesPresenter = new CrimeListPresenterImpl(policeClient, locationProvider);
        crimesPresenter.attach(crimesView);
    }

    @Test
    public void onAttachShouldAssignView() {
        crimesPresenter.attach(crimesView);
        assertThat(crimesPresenter.crimesView, is(equalTo(crimesView)));
    }

    @Test
    public void shouldDetachView() {
        crimesPresenter.detach();
        assertThat(crimesPresenter.crimesView, is(nullValue()));
    }

    @Test
    public void shouldRequestLocationOnStart() throws CurrentLocationProvider.MissingPermissionException {
        crimesPresenter.onStart();
        verify(locationProvider).connect();
        verify(locationProvider).requestCurrentLocation(crimesPresenter);
    }

    @Test
    public void shouldDisconnectLocationProviderOnStop() {
        crimesPresenter.onStop();
        verify(locationProvider).disconnect();
    }

    @Test
    public void shouldRequestLocationPermissionOnMissing() throws CurrentLocationProvider.MissingPermissionException {
        doThrow(CurrentLocationProvider.MissingPermissionException.class)
                .when(locationProvider).requestCurrentLocation(crimesPresenter);
        crimesPresenter.onStart();
        verify(crimesView).requestLocationPermission(crimesPresenter);
    }

    @Test
    public void shouldShowTheLocationWhenObtained() {
        double lat = 23d;
        double lng = 65d;
        crimesPresenter.onLocationObtained(lat, lng);
        verify(crimesView).showCurrentLocation(lat, lng);
        Mockito.verifyZeroInteractions(policeClient);
    }

    @Test
    public void shouldRequestCrimesOnMapBoundsChange() {
        int southEastLat = 55;
        int southEastLng = 0;
        double northEastLat = 55.3;
        double northEastLng = 0.5;
        crimesPresenter.mapBoundsChanged(southEastLat, southEastLng, northEastLat, northEastLng);
        verify(policeClient)
                .requestCrimesByRectangularBounds(southEastLat, southEastLng, northEastLat, northEastLng, crimesPresenter);
        verify(crimesView).showProgress();
    }

    @Test
    public void shouldHanldeLocationPermissionDeniedError() {
        shouldHandlePermissionDeniedOnFirstRequestError();
        crimesPresenter.onLocationRequestError(Mockito.mock(Throwable.class));
        verify(crimesView).showLocationPermissionDeniedError();
        verify(locationProvider).requestDefaultLocation(eq(crimesPresenter));
    }

    @Test
    public void shouldHandlePermissionDeniedOnFirstRequestError() {
        when(crimesView.hasLocationPersmission()).thenReturn(false);
        crimesPresenter.onLocationRequestError(Mockito.mock(Throwable.class));
        verify(crimesView).requestLocationPermission(eq(crimesPresenter));
    }

    @Test
    public void shouldShowLocationUnavailable() {
        when(crimesView.hasLocationPersmission()).thenReturn(true);
        crimesPresenter.onLocationRequestError(Mockito.mock(Throwable.class));
        verify(crimesView).showLocationUnavailableError();
        verify(locationProvider).requestDefaultLocation(eq(crimesPresenter));
    }

    @Test
    public void shouldRequestCurrentLocationOnLocationPermissionGranted() throws CurrentLocationProvider.MissingPermissionException {
        crimesPresenter.onLocationPermissionGranted();
        verify(locationProvider).requestCurrentLocation(eq(crimesPresenter));
    }

    @Test
    public void shouldRequestDefaultLocationOnLocationPermissionGrantedSecurityException() throws CurrentLocationProvider.MissingPermissionException {
//        when(locationProvider.requestCurrentLocation(crimesPresenter)).thenThrow()
        doThrow(CurrentLocationProvider.MissingPermissionException.class).when(locationProvider)
                                                                         .requestCurrentLocation(crimesPresenter);
        crimesPresenter.onLocationPermissionGranted();
        verify(locationProvider).requestCurrentLocation(crimesPresenter);
        verify(locationProvider).requestDefaultLocation(crimesPresenter);
    }

    @Test
    public void shouldHandleLocationPermissionDenied() {
        crimesPresenter.onLocationPermissionDenied();
        verify(crimesView).showLocationPermissionDeniedError();
        verify(locationProvider).requestDefaultLocation(eq(crimesPresenter));
    }

    @Test
    public void shouldDisplayLoadedCrimes() {
        Crime crime1 = new Crime();
        crime1.setCategory("one");
        Crime crime2 = new Crime();
        crime2.setCategory("two");
        List<Crime> crimes = Arrays.asList(crime1, crime2);
        crimesPresenter.onCrimesLoadComplete(crimes);
        verify(crimesView).addClusteredCrimes(crimes);
        verify(crimesView).hideProgress();
    }

    @Test
    public void shouldInformNoCrimes() {
        crimesPresenter.onCrimesLoadComplete(new ArrayList<Crime>());
        verify(crimesView).showNoCrimesMessage();
        verify(crimesView).hideProgress();

    }

    @Test
    public void shouldShowErrorOnCrimeLoadIssue() {
        crimesPresenter.onCrimesLoadError(mock(Throwable.class));
        crimesPresenter.onServerError("");
        crimesPresenter.onOtherError("");
        crimesPresenter.onUserError("");
        crimesPresenter.onTooManyRequests();
        crimesPresenter.onReadTimeOut(new Exception());
        verify(crimesView, times(6)).hideProgress();
        verify(crimesView, times(6)).showCrimesLoadingError();
    }
}
