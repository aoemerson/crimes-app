package io.github.aoemerson.crimesmvp.view;

import android.support.annotation.StringRes;

import java.util.List;

import io.github.aoemerson.crimesmvp.model.data.Crime;

public interface CrimesView {

    interface LocationPermissionRequestCallback {
        void onLocationPermissionGranted();
        void onLocationPermissionDenied();
    }

    void setCrimes(List<Crime> crimes);
    void showProgress();
    void hideProgress();
    void requestLocationPermission(LocationPermissionRequestCallback callback);
    boolean isProgressShown();

    void showError(@StringRes int errResId);
}
