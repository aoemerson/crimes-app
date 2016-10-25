package io.github.aoemerson.crimesmvp.presenter;

import android.annotation.SuppressLint;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import javax.inject.Inject;

import io.github.aoemerson.crimesmvp.model.PoliceClient;
import io.github.aoemerson.crimesmvp.model.data.Crime;
import io.github.aoemerson.crimesmvp.model.data.CrimeTranslator;
import io.github.aoemerson.crimesmvp.model.location.CurrentLocationProvider;
import io.github.aoemerson.crimesmvp.model.location.GoogleFusedLocationProvider;
import io.github.aoemerson.crimesmvp.view.CrimesView;
import timber.log.Timber;


public class CrimeListPresenterImpl implements CrimeListPresenter {

    final PoliceClient policeClient;
    CrimesView crimesView;
    private CurrentLocationProvider locationProvider;
    private CrimeTranslator crimeTranslator;
    private boolean askedForLocationPermission;
    @SuppressLint("UseSparseArrays")
    private HashMap<Long, Crime> crimesAlreadyAdded = new HashMap<>();

    @Inject
    CrimeListPresenterImpl(PoliceClient policeClient, CurrentLocationProvider locationProvider, CrimeTranslator crimeTranslator) {
        this.policeClient = policeClient;
        this.locationProvider = locationProvider;
        this.crimeTranslator = crimeTranslator;
    }


    @Override
    public void mapBoundsChanged(double southWestLat, double southWestLng, double northEastLat, double northEastLng) {
        crimesView.showProgress();
        Timber.d("Loading the following area from web: %nSW: [%f, %f]%nNE: [%f, %f]", southWestLat, southWestLng, northEastLat, northEastLng);
        Timber.d("--------> %f%n|%n|%n|%nv%n%f", northEastLng - southWestLng, northEastLat - southWestLat);
        policeClient
                .requestCrimesByRectangularBounds(southWestLat, southWestLng, northEastLat, northEastLng, this);
    }

    @Override
    public void onStart() {
        locationProvider.connect();
        try {
            locationProvider.requestCurrentLocation(this);
        } catch (GoogleFusedLocationProvider.MissingPermissionException e) {
            crimesView.requestLocationPermission(this);
        }
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
    public void crimesGroupClicked(double lat, double lng, long[] crimeIds) {
        ArrayList<Crime> crimesToShow = new ArrayList<>();
        for (long id : crimeIds) {
            if (crimesAlreadyAdded.containsKey(id))
                crimesToShow.add(crimeTranslator.translate(crimesAlreadyAdded.get(id)));
        }
        if (crimesToShow.size() > 0) {
            crimesView.showCrimes(crimesToShow);
        }
    }

    @Override
    public void crimeMarkerClicked(long id) {
        crimesView.showCrime(crimeTranslator.translate(crimesAlreadyAdded.get(id)));
    }

    @Override
    public void onCrimesLoadComplete(List<Crime> crimes) {
        Timber.d("Finished loading %d crimes.", crimes.size());
        if (crimesView != null) {
            if (crimes != null && crimes.size() > 0) {
                checkAndAddCrimes(crimes);
            } else {
                crimesView.showNoCrimesMessage();
            }
            crimesView.hideProgress();
        }
    }

    private void checkAndAddCrimes(List<Crime> crimes) {
        int count = 0;
        long start = System.nanoTime();
        for (Crime crime : crimes) {
            if (!crimesAlreadyAdded.containsKey(crime.getId())) {
                crimesAlreadyAdded.put(crime.getId(), crime);
                crimesView.addCrime(crimeTranslator.translate(crime));
                count++;
            }
        }
        long end = System.nanoTime();
        if (count > 0) {
            crimesView.finishedAddingCrimes();
        }
//        Timber.v("%d %s [+%.1fms]%n%s", response.code(), response.request()
//                                                                 .url(), (t2 - t1) / 1e6d,

        Timber.d("Added %d crimes to the display in %.1fms (%d crimes already added, %d in memory)",
                count, (end - start) / 1e6d, crimes.size() - count, crimesAlreadyAdded.size());
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
    public void onQueryAreaTooLarge() {

    }


    @Override
    public void onLocationObtained(double lat, double lng) {
        crimesView.showCurrentLocation(lat, lng);
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
        try {
            locationProvider.requestCurrentLocation(this);
        } catch (GoogleFusedLocationProvider.MissingPermissionException e) {
            locationProvider.requestDefaultLocation(this);
        }
    }

    @Override
    public void onLocationPermissionDenied() {
        crimesView.showLocationPermissionDeniedError();
        locationProvider.requestDefaultLocation(this);
    }
}
