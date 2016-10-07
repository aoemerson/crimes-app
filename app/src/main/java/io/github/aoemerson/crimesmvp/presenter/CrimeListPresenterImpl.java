package io.github.aoemerson.crimesmvp.presenter;

import java.util.List;

import io.github.aoemerson.crimesmvp.model.data.Crime;
import io.github.aoemerson.crimesmvp.model.location.CurrentLocationProvider;
import io.github.aoemerson.crimesmvp.model.PoliceClient;
import io.github.aoemerson.crimesmvp.model.PoliceClientImpl;
import io.github.aoemerson.crimesmvp.view.CrimesView;

/**
 * Created by Andrew on 17/08/2016.
 */
public class CrimeListPresenterImpl implements CrimeListPresenter, PoliceClient.OnCrimesLoadedListener, CurrentLocationProvider.LocationProvidedCallback {

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
        locationProvider.requestCurrentLocation(this);
    }

    @Override
    public void onLoadComplete(List<Crime> crimes) {
        if (crimesView != null) {
            crimesView.setCrimes(crimes);
            crimesView.hideProgress();
        }
    }

    @Override
    public void onLoadError(Throwable t) {

    }

    @Override
    public void onLocationObtained(float lat, float lng) {
        policeClient.requestCrimesByPoint(lat,lng, this);
    }
}
