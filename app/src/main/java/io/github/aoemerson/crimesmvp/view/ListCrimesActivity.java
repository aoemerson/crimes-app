package io.github.aoemerson.crimesmvp.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import aoemeron.github.io.crimesmvp.R;
import io.github.aoemerson.crimesmvp.application.ApplicationModule;
import io.github.aoemerson.crimesmvp.model.data.Crime;
import io.github.aoemerson.crimesmvp.presenter.CrimeListPresenterImpl;
import io.github.aoemerson.crimesmvp.presenter.DaggerLocalCrimesComponent;

public class ListCrimesActivity extends AppCompatActivity implements CrimesView {

    private static final int LOCATION_PERM_REQ_CODE = 1;
    @Inject CrimeListPresenterImpl crimeListPresenter;
    private ProgressBar progressBar;
    private ListView listView;
    private CrimeListViewAdapter crimeListViewAdapter;
    private LocationPermissionRequestCallback locationRequestCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_crimes);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        listView = (ListView) findViewById(R.id.list);
        crimeListViewAdapter = new CrimeListViewAdapter(this);
        listView.setAdapter(crimeListViewAdapter);
        DaggerLocalCrimesComponent.builder()
                                  .applicationModule(new ApplicationModule(getApplication()))
                                  .build()
                                  .inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        crimeListPresenter.attach(this);
        crimeListPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        crimeListPresenter.onStop();
        crimeListPresenter.detach();

    }

    @Override
    protected void onResume() {
        super.onResume();
        crimeListPresenter.onRequestLocalCrimes();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERM_REQ_CODE && locationRequestCallback != null) {
            boolean granted = false;
            for (int result : grantResults) {
                granted = granted || (result == PackageManager.PERMISSION_GRANTED);
            }
            if (granted)
                locationRequestCallback.onLocationPermissionGranted();
            else
                locationRequestCallback.onLocationPermissionDenied();
        }
    }

    @Override
    public void setCrimes(List<Crime> crimes) {
        crimeListViewAdapter.addAll(crimes);
        crimeListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNoCrimesMessage() {
        showError(R.string.msg_no_crimes_here);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void requestLocationPermission(LocationPermissionRequestCallback callback) {
        if (!hasLocationPersmission()) {
            locationRequestCallback = callback;
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERM_REQ_CODE);

        } else {
            callback.onLocationPermissionGranted();
        }

    }

    @Override
    public boolean isProgressShown() {
        return progressBar.getVisibility() == View.VISIBLE;
    }

    @Override
    public void showCrimesLoadingError() {
        showError(R.string.err_could_not_load_crimes);
    }

    @Override
    public void showLocationPermissionDeniedError() {
        showError(R.string.err_location_permission_denied);
    }

    @Override
    public void showLocationUnavailableError() {
        showError(R.string.err_location_not_obtained);
    }

    @Override
    public boolean hasLocationPersmission() {
        return ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void showError(@StringRes int errResId) {
        Toast.makeText(this, getString(errResId), Toast.LENGTH_SHORT).show();
    }

}
