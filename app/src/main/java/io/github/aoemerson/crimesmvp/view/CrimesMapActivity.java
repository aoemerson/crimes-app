package io.github.aoemerson.crimesmvp.view;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import javax.inject.Inject;

import aoemerson.github.io.crimesmvp.R;
import butterknife.ButterKnife;
import io.github.aoemerson.crimesmvp.application.ApplicationModule;
import io.github.aoemerson.crimesmvp.model.data.Crime;
import io.github.aoemerson.crimesmvp.presenter.CrimeListPresenterImpl;
import io.github.aoemerson.crimesmvp.presenter.DaggerLocalCrimesComponent;

public class CrimesMapActivity extends BaseActivity implements OnMapReadyCallback {


    @Inject CrimeListPresenterImpl crimePresenter;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crimes_map);
        ButterKnife.bind(this);

        DaggerLocalCrimesComponent.builder()
                                  .applicationModule(new ApplicationModule(getApplication()))
                                  .build()
                                  .inject(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /*
onCreate(), onStart(), onResume(), onPause(), onStop(), onDestroy(), onSaveInstanceState(), and onLowMemory()     */

    @Override
    protected void onStart() {
        super.onStart();
        crimePresenter.attach(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        crimePresenter.detach();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void setCrimes(List<Crime> crimes) {
//        for (Crime crime : crimes) {
//
//            googleMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(crime.getLocation().getLatitude(), crime.getLocation().getLongitude()))
//                    .title(crime.getCategory()));
//        }
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

    @Override
    public void showCurrentLocation(double lat, double lng) {
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15f));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        crimePresenter.onStart();
        crimePresenter.onRequestLocalCrimes();

    }
}
