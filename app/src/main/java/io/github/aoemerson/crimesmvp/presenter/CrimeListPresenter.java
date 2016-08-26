package io.github.aoemerson.crimesmvp.presenter;

/**
 * Created by Andrew on 17/08/2016.
 */
public interface CrimeListPresenter {

    void onRequestCrimes(float latitude, float longitude);
    void onRequestLocalCrimes();

}
