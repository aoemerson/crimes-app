package io.github.aoemerson.crimesmvp.view;

import java.util.List;

import io.github.aoemerson.crimesmvp.model.Crime;

/**
 * Created by Andrew on 17/08/2016.
 */
public interface CrimesView {

    void setCrimes(List<Crime> crimes);
    void showProgress();
    void hideProgress();
    boolean isProgressShown();

}
