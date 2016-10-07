package io.github.aoemerson.crimesmvp.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import static org.junit.Assert.assertEquals;
import org.mockito.runners.MockitoJUnitRunner;


import java.util.Arrays;
import java.util.List;

import io.github.aoemerson.crimesmvp.model.data.Crime;
import io.github.aoemerson.crimesmvp.model.location.CurrentLocationProvider;
import io.github.aoemerson.crimesmvp.model.PoliceClient;
import io.github.aoemerson.crimesmvp.view.CrimesView;

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
        crimesPresenter = new CrimeListPresenterImpl(crimesView, policeClient, locationProvider);
    }

    @Test
    public void shouldShowProgessOnRequestCrimes() {
        crimesPresenter.onRequestCrimes(1f, 1f);
        verify(crimesView, times(1)).showProgress();
    }

    @Test
    public void shouldRequestCrimesByPointForView() {
        float latitude = 1f;
        float longitude = 2f;
        crimesPresenter.onRequestCrimes(latitude, longitude);
        ArgumentCaptor<Float> latCaptor = forClass(Float.class);
        ArgumentCaptor<Float> longCaptor = forClass(Float.class);
        verify(policeClient, times(1)).requestCrimesByPoint(latCaptor.capture(), longCaptor.capture(), eq(crimesPresenter));
        assertEquals(Float.valueOf(latitude), latCaptor.getValue());
        assertEquals(Float.valueOf(longitude), longCaptor.getValue());
    }

    @Test
    public void shouldPassCrimesToViewOnLoaded() {
        Crime crime1 = new Crime();
        crime1.setCategory("one");
        Crime crime2 = new Crime();
        crime2.setCategory("two");
        List<Crime> crimes = Arrays.asList(crime1, crime2);
        crimesPresenter.onLoadComplete(crimes);
        verify(crimesView, times(1)).setCrimes(crimes);
        verify(crimesView, times(1)).hideProgress();
    }

    @Test
    public void shouldGetLocalCrimes() {
        crimesPresenter.onRequestLocalCrimes();
        verify(crimesView, times(1)).showProgress();
        verify(locationProvider, times(1)).requestCurrentLocation(eq(crimesPresenter));
//        verify(policeClient,times(1)).requestCrimesByPoint(anyFloat(), anyFloat(), any(PoliceClient.OnCrimesLoadedListener.class));
    }

    @Test
    public void shouldRequestCrimesOnLocationObtained() {
        float lat = 23f;
        float lng = 65f;
        crimesPresenter.onLocationObtained(lat, lng);
        verify(policeClient, times(1)).requestCrimesByPoint(eq(lat), eq(lng), any(PoliceClient.OnCrimesLoadedListener.class));
    }
}
