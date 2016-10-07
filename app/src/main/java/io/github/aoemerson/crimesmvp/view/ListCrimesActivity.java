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

import aoemeron.github.io.crimesmvp.R;
import io.github.aoemerson.crimesmvp.model.data.Crime;
import io.github.aoemerson.crimesmvp.presenter.CrimeListPresenter;
import io.github.aoemerson.crimesmvp.presenter.CrimeListPresenterImpl;

public class ListCrimesActivity extends AppCompatActivity implements CrimesView {

    private static final int LOCATION_PERM_REQ_CODE = 1;
    private ProgressBar progressBar;
    private ListView listView;
    private CrimeListPresenter crimeListPresenter;
    private CrimeListViewAdapter crimeListViewAdapter;
    private LocationPermissionRequestCallback locationRequestCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_crimes);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        listView = (ListView) findViewById(R.id.list);
        crimeListPresenter = new CrimeListPresenterImpl(this);
        crimeListViewAdapter = new CrimeListViewAdapter(this);
        listView.setAdapter(crimeListViewAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        crimeListPresenter.onRequestCrimes(52.629729f, -1.131592f);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERM_REQ_CODE && locationRequestCallback != null) {
            boolean granted = true;
            for (int result : grantResults) {
                granted = granted && (result == PackageManager.PERMISSION_GRANTED);
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
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void requestLocationPermission(LocationPermissionRequestCallback callback) {
        if (ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    public void showError(@StringRes int errResId) {
        Toast.makeText(this, getString(errResId), Toast.LENGTH_SHORT).show();
    }
}
