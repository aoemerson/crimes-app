package io.github.aoemerson.crimesmvp.model;

import java.util.List;

import io.github.aoemerson.crimesmvp.model.data.Crime;

public interface PoliceClient {

    interface OnCrimesLoadedListener {
        void onCrimesLoadComplete(List<Crime> crimes);
        void onCrimesLoadError(Throwable t);

        void onServerError(String reason);

        void onOtherError(String reason);

        void onUserError(String reason);

        void onTooManyRequests();

        void onReadTimeOut(Throwable cause);

        void onQueryAreaTooLarge();
    }

    void requestCrimesByPoint(double lat, double lng, OnCrimesLoadedListener listener);

    void requestCrimesByRectangularBounds(double southEastLat, double southEastLng, double northEastLat, double northEastLng, OnCrimesLoadedListener listener);

}
