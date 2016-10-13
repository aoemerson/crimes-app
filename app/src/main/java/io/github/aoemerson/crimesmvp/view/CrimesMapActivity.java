package io.github.aoemerson.crimesmvp.view;

import android.os.Bundle;

import java.util.List;

import aoemeron.github.io.crimesmvp.R;
import io.github.aoemerson.crimesmvp.model.data.Crime;

public class CrimesMapActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crimes_map);
    }


    @Override
    public void setCrimes(List<Crime> crimes) {

    }

    @Override
    public void showNoCrimesMessage() {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public boolean isProgressShown() {
        return false;
    }

    @Override
    public void showCrimesLoadingError() {

    }

    @Override
    public void showLocationPermissionDeniedError() {

    }

    @Override
    public void showLocationUnavailableError() {

    }
}
