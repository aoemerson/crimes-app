package io.github.aoemerson.crimesmvp.model;

import java.util.List;

import io.github.aoemerson.crimesmvp.model.data.Crime;

public interface PoliceClient {

    interface OnCrimesLoadedListener {
        void onCrimesLoadComplete(List<Crime> crimes);
        void onCrimesLoadError(Throwable t);

    }

    void requestCrimesByPoint(double lat, double lng, OnCrimesLoadedListener listener);

}
