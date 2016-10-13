package io.github.aoemerson.crimesmvp.presenter;

import io.github.aoemerson.crimesmvp.model.PoliceClient;
import io.github.aoemerson.crimesmvp.model.location.CurrentLocationProvider;
import io.github.aoemerson.crimesmvp.view.CrimesView;

public interface CrimeListPresenter extends PoliceClient.OnCrimesLoadedListener, CurrentLocationProvider.LocationRequestCallback, CrimesView.LocationPermissionRequestCallback {

    void onRequestCrimes(double latitude, double longitude);
    void onRequestLocalCrimes();
    void onStart();
    void onStop();
    void attach(CrimesView view);
    void detach();

}
