package io.github.aoemerson.crimesmvp.model;

import java.util.List;

/**
 * Created by Andrew on 17/08/2016.
 */
public interface PoliceClient {

    interface OnCrimesLoadedListener {
        void onLoadComplete(List<Crime> crimes);
        void onLoadError(Throwable t);

    }

    void requestCrimesByPoint(float lat, float lng, OnCrimesLoadedListener listener);

}
