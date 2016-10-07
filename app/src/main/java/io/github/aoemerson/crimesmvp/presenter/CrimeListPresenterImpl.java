package io.github.aoemerson.crimesmvp.presenter;

import java.util.List;

import aoemeron.github.io.crimesmvp.R;
import io.github.aoemerson.crimesmvp.model.data.Crime;
import io.github.aoemerson.crimesmvp.model.location.CurrentLocationProvider;
import io.github.aoemerson.crimesmvp.model.PoliceClient;
import io.github.aoemerson.crimesmvp.model.PoliceClientImpl;
import io.github.aoemerson.crimesmvp.model.location.GoogleFusedLocationProvider;
import io.github.aoemerson.crimesmvp.view.CrimesView;

public class CrimeListPresenterImpl implements CrimeListPresenter, PoliceClient.OnCrimesLoadedListener, CurrentLocationProvider.LocationRequestCallback, CrimesView.LocationPermissionRequestCallback {

    final CrimesView crimesView;
    final PoliceClient policeClient;
    private CurrentLocationProvider locationProvider;

    public CrimeListPresenterImpl(CrimesView crimesView) {
        this.crimesView = crimesView;
        this.policeClient = new PoliceClientImpl();
    }

    CrimeListPresenterImpl(CrimesView crimesView, PoliceClient policeClient, CurrentLocationProvider locationProvider) {
        this.crimesView = crimesView;
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
    public void onCrimesLoadComplete(List<Crime> crimes) {
        if (crimesView != null) {
            crimesView.setCrimes(crimes);
            crimesView.hideProgress();
        }
    }

    @Override
    public void onCrimesLoadError(Throwable t) {
        crimesView.showError(R.string.err_could_not_load_crimes);
    }



    @Override
    public void onLocationObtained(double lat, double lng) {
        policeClient.requestCrimesByPoint(lat, lng, this);
    }

    @Override
    public void onLocationRequestError(Throwable e) {
        crimesView.showError(R.string.err_location_not_obtained);
    }

    @Override
    public void onLocationPermissionGranted() {
        onRequestLocalCrimes();
    }

    @Override
    public void onLocationPermissionDenied() {
        crimesView.showError(R.string.err_location_permission_denied);
    }
}
