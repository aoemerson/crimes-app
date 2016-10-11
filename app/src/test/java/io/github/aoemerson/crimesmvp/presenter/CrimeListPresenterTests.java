package io.github.aoemerson.crimesmvp.presenter;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;

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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
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
    public void shouldConnectLocationProviderOnStart() throws CurrentLocationProvider.MissingPermissionException {
        crimesPresenter.onStart();
        verify(locationProvider, once()).connect();
    }

    @NonNull
    private VerificationMode once() {
        return times(1);
    }

    @Test
    public void shouldDisconnectLocationProviderOnStop() {
        crimesPresenter.onStop();
        verify(locationProvider, once()).disconnect();
    }

    @Test
    public void onRequestLocalCrimesShouldFineLocation() throws CurrentLocationProvider.MissingPermissionException {
        crimesPresenter.onRequestLocalCrimes();
        verify(crimesView, once()).showProgress();
        verify(locationProvider, once()).requestCurrentLocation(eq(crimesPresenter));
    }

    @Test
    public void shouldRequestLocationPermissionOnMissing() throws CurrentLocationProvider.MissingPermissionException {
        doThrow(CurrentLocationProvider.MissingPermissionException.class)
                .when(locationProvider).requestCurrentLocation(crimesPresenter);
        crimesPresenter.onRequestLocalCrimes();
        verify(crimesView, once()).requestLocationPermission(crimesPresenter);
    }

    @Test
    public void shouldRequestCrimesOnLocationObtained() {
        float lat = 23f;
        float lng = 65f;
        crimesPresenter.onLocationObtained(lat, lng);
        verify(policeClient, once())
                .requestCrimesByPoint(eq(((double) lat)), eq(((double) lng)), eq(crimesPresenter));
    }

    @Test
    public void shouldHanldeLocationPermissionDeniedError() {
        shouldHandlePermissionDeniedOnFirstRequestError();
        crimesPresenter.onLocationRequestError(Mockito.mock(Throwable.class));
        verify(crimesView, once()).showLocationPermissionDeniedError();
        verify(locationProvider, once()).requestDefaultLocation(eq(crimesPresenter));
    }

    @Test
    public void shouldHandlePermissionDeniedOnFirstRequestError() {
        when(crimesView.hasLocationPersmission()).thenReturn(false);
        crimesPresenter.onLocationRequestError(Mockito.mock(Throwable.class));
        verify(crimesView, once()).requestLocationPermission(eq(crimesPresenter));
    }

    @Test
    public void shouldShowLocationUnavailable() {
        when(crimesView.hasLocationPersmission()).thenReturn(true);
        crimesPresenter.onLocationRequestError(Mockito.mock(Throwable.class));
        verify(crimesView, once()).showLocationUnavailableError();
        verify(locationProvider, once()).requestDefaultLocation(eq(crimesPresenter));
    }

    @Test
    public void shouldRequestLocalCrimesOnLocationPermissionGranted() throws CurrentLocationProvider.MissingPermissionException {
        crimesPresenter.onLocationPermissionGranted();
        verify(crimesView, once()).showProgress();
        verify(locationProvider, once()).requestCurrentLocation(eq(crimesPresenter));
    }

    @Test
    public void shouldHandleLocationPermissionDenied() {
        crimesPresenter.onLocationPermissionDenied();
        verify(crimesView, once()).showLocationPermissionDeniedError();
        verify(locationProvider, once()).requestDefaultLocation(eq(crimesPresenter));
    }

    @Test
    public void shouldDisplayLoadedCrimes() {
        Crime crime1 = new Crime();
        crime1.setCategory("one");
        Crime crime2 = new Crime();
        crime2.setCategory("two");
        List<Crime> crimes = Arrays.asList(crime1, crime2);
        crimesPresenter.onCrimesLoadComplete(crimes);
        verify(crimesView, once()).setCrimes(crimes);
        verify(crimesView, once()).hideProgress();
    }

    @Test
    public void shouldInformNoCrimes() {
        crimesPresenter.onCrimesLoadComplete(new ArrayList<Crime>());
        verify(crimesView, once()).showNoCrimesMessage();
        verify(crimesView, once()).hideProgress();

    }

    @Test
    public void shouldShowErrorOnCrimeLoadIssue() {
        crimesPresenter.onCrimesLoadError(mock(Throwable.class));
        verify(crimesView, once()).hideProgress();
        verify(crimesView, once()).showCrimesLoadingError();
    }

    /*

    Old tests

     */

    @Test
    public void shouldShowProgessOnRequestCrimes() {
        crimesPresenter.onRequestCrimes(1d, 1d);
        verify(crimesView, once()).showProgress();
    }

    @Test
    public void shouldRequestCrimesByPointForView() {
        double latitude = 1d;
        double longitude = 2d;
        crimesPresenter.onRequestCrimes(latitude, longitude);
        ArgumentCaptor<Double> latCaptor = forClass(Double.class);
        ArgumentCaptor<Double> longCaptor = forClass(Double.class);
        verify(policeClient, once()).requestCrimesByPoint(latCaptor.capture(), longCaptor
                .capture(), eq(crimesPresenter));
        assertEquals(Double.valueOf(latitude), latCaptor.getValue());
        assertEquals(Double.valueOf(longitude), longCaptor.getValue());
    }



    @Test
    public void shouldGetLocalCrimes() throws CurrentLocationProvider.MissingPermissionException {
        crimesPresenter.onRequestLocalCrimes();
        verify(crimesView, once()).showProgress();
        verify(locationProvider, once()).requestCurrentLocation(eq(crimesPresenter));
//        verify(policeClient,times(1)).requestCrimesByPoint(anyFloat(), anyFloat(), any(PoliceClient.OnCrimesLoadedListener.class));
    }


}
