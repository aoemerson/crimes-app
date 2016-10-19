package io.github.aoemerson.crimesmvp.presenter;

import io.github.aoemerson.crimesmvp.model.PoliceClient;
import io.github.aoemerson.crimesmvp.model.location.CurrentLocationProvider;
import io.github.aoemerson.crimesmvp.view.CrimesView;

public interface CrimeListPresenter extends PoliceClient.OnCrimesLoadedListener, CurrentLocationProvider.LocationRequestCallback, CrimesView.LocationPermissionRequestCallback {

    void mapBoundsChanged(double southEastLat, double southEastLng, double northEastLat, double northEastLng);
    void onStart();
    void onStop();
    void attach(CrimesView view);
    void detach();

}
