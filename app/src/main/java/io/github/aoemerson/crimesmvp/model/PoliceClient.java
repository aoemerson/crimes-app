package io.github.aoemerson.crimesmvp.model;

import java.util.List;

import io.github.aoemerson.crimesmvp.model.data.Crime;

public interface PoliceClient {

    interface OnCrimesLoadedListener {
        void onLoadComplete(List<Crime> crimes);
        void onLoadError(Throwable t);

    }

    void requestCrimesByPoint(float lat, float lng, OnCrimesLoadedListener listener);

}
