package io.github.aoemerson.crimesmvp.presenter;

import java.util.List;

import javax.inject.Inject;

import aoemeron.github.io.crimesmvp.R;
import io.github.aoemerson.crimesmvp.model.PoliceClient;
import io.github.aoemerson.crimesmvp.model.PoliceClientImpl;
import io.github.aoemerson.crimesmvp.model.data.Crime;
import io.github.aoemerson.crimesmvp.model.location.CurrentLocationProvider;
import io.github.aoemerson.crimesmvp.model.location.GoogleFusedLocationProvider;
import io.github.aoemerson.crimesmvp.view.CrimesView;


public class CrimeListPresenterImpl implements CrimeListPresenter, PoliceClient.OnCrimesLoadedListener, CurrentLocationProvider.LocationRequestCallback, CrimesView.LocationPermissionRequestCallback {

    final PoliceClient policeClient;
    CrimesView crimesView;
    private CurrentLocationProvider locationProvider;
    private boolean askedForLocationPermission;

    public CrimeListPresenterImpl(CrimesView crimesView) {
        this.crimesView = crimesView;
        this.policeClient = new PoliceClientImpl();
    }

    @Inject
    CrimeListPresenterImpl(PoliceClient policeClient, CurrentLocationProvider locationProvider) {
        this.policeClient = policeClient;
        this.locationProvider = locationProvider;
    }

    @Override
    public void onRequestCrimes(float latitude, float longitude) {
        crimesView.showProgress();
        policeClient.requestCrimesByPoint(latitude, longitude, this);
    }

    @Override
    public void onRequestLocalCrimes() {
        crimesView.showProgress();
        try {
            locationProvider.requestCurrentLocation(this);
        } catch (GoogleFusedLocationProvider.MissingPermissionException e) {
            crimesView.requestLocationPermission(this);
        }
    }

    @Override
    public void onStart() {
        locationProvider.connect();
    }

    @Override
    public void onStop() {
        locationProvider.disconnect();
    }

    @Override
    public void attach(CrimesView view) {
        this.crimesView = view;
    }

    @Override
    public void detach() {
        crimesView = null;
    }

    @Override
    public void onCrimesLoadComplete(List<Crime> crimes) {
        if (crimesView != null) {
            crimesView.setCrimes(crimes);
            crimesView.hideProgress();
        }
    }

    @Override
    public void onCrimesLoadError(Throwable t) {
        crimesView.showCrimesLoadingError();
    }


    @Override
    public void onLocationObtained(double lat, double lng) {
        policeClient.requestCrimesByPoint(lat, lng, this);
    }

    @Override
    public void onLocationRequestError(Throwable e) {
        if (!askedForLocationPermission && !crimesView.hasLocationPersmission()) {
            crimesView.requestLocationPermission(this);
            askedForLocationPermission = true;
        } else if (!crimesView.hasLocationPersmission()) {
            crimesView.showLocationPermissionDeniedError();
            locationProvider.requestDefaultLocation(this);
        } else {
            crimesView.showLocationUnavailableError();
            locationProvider.requestDefaultLocation(this);
        }
    }

    @Override
    public void onLocationPermissionGranted() {
        onRequestLocalCrimes();
    }

    @Override
    public void onLocationPermissionDenied() {
        crimesView.showLocationPermissionDeniedError();
    }
}
