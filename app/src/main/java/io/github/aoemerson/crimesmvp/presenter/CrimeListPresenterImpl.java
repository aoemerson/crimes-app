package io.github.aoemerson.crimesmvp.presenter;

import java.util.List;

import javax.inject.Inject;

import io.github.aoemerson.crimesmvp.model.PoliceClient;
import io.github.aoemerson.crimesmvp.model.data.Crime;
import io.github.aoemerson.crimesmvp.model.location.CurrentLocationProvider;
import io.github.aoemerson.crimesmvp.model.location.GoogleFusedLocationProvider;
import io.github.aoemerson.crimesmvp.view.CrimesView;


public class CrimeListPresenterImpl implements CrimeListPresenter {

    final PoliceClient policeClient;
    CrimesView crimesView;
    private CurrentLocationProvider locationProvider;
    private boolean askedForLocationPermission;

    @Inject
    CrimeListPresenterImpl(PoliceClient policeClient, CurrentLocationProvider locationProvider) {
        this.policeClient = policeClient;
        this.locationProvider = locationProvider;
    }

    @Override
    public void onRequestCrimes(double latitude, double longitude) {
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
            if (crimes != null && crimes.size() > 0) {
                crimesView.setCrimes(crimes);
            } else {
                crimesView.showNoCrimesMessage();
            }
            crimesView.hideProgress();
        }
    }

    @Override
    public void onCrimesLoadError(Throwable t) {
        updateViewOnError();
    }

    private void updateViewOnError() {
        crimesView.hideProgress();
        crimesView.showCrimesLoadingError();
    }

    @Override
    public void onServerError(String reason) {
        updateViewOnError();
    }

    @Override
    public void onOtherError(String reason) {
        updateViewOnError();
    }

    @Override
    public void onUserError(String reason) {
        updateViewOnError();
    }

    @Override
    public void onTooManyRequests() {
        updateViewOnError();
    }

    @Override
    public void onReadTimeOut(Throwable cause) {
        updateViewOnError();
    }



    @Override
    public void onLocationObtained(double lat, double lng) {
        crimesView.showCurrentLocation(lat, lng);
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
        locationProvider.requestDefaultLocation(this);
    }
}
