package io.github.aoemerson.crimesmvp.model;

/**
 * Created by Andrew on 17/08/2016.
 */
public interface CurrentLocationProvider {
    interface LocationProvidedCallback {
        void onLocationObtained(float lat, float lng);
    }

    void requestCurrentLocation(LocationProvidedCallback callback);
    void onStart();
    void onStop();
}
