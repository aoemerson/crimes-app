package io.github.aoemerson.crimesmvp.presenter;

import io.github.aoemerson.crimesmvp.view.CrimesView;

/**
 * Created by Andrew on 17/08/2016.
 */
public interface CrimeListPresenter {

    void onRequestCrimes(float latitude, float longitude);
    void onRequestLocalCrimes();
    void onStart();
    void onStop();
    void attach(CrimesView view);
    void detach();

}
