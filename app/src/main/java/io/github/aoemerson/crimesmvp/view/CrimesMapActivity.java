package io.github.aoemerson.crimesmvp.view;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import javax.inject.Inject;

import aoemerson.github.io.crimesmvp.R;
import butterknife.ButterKnife;
import io.github.aoemerson.crimesmvp.application.ApplicationModule;
import io.github.aoemerson.crimesmvp.model.data.Crime;
import io.github.aoemerson.crimesmvp.model.data.CrimeClusterItem;
import io.github.aoemerson.crimesmvp.presenter.CrimeListPresenterImpl;
import io.github.aoemerson.crimesmvp.presenter.DaggerLocalCrimesComponent;

public class CrimesMapActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnMarkerClickListener {


    @Inject CrimeListPresenterImpl crimePresenter;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;

    private ClusterManager<CrimeClusterItem> clusterManager;

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
    public void addClusteredCrimes(List<Crime> crimes) {
        for (Crime crime : crimes) {

            addCrime(crime);
        }
    }

    @Override
    public void addCrime(Crime crime) {
        clusterManager.addItem(new CrimeClusterItem(crime));
//        clusterManager.cluster();
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
        LatLng latLng = new LatLng(lat, lng);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
//        try {
//            googleMap.setMyLocationEnabled(true);
//        } catch (SecurityException e) {
//            googleMap.addCircle(new CircleOptions()
//                    .center(latLng)
//                    .clickable(false)
//                    .fillColor(Color.BLUE)
//                    .radius(20)
//                    .strokeWidth(0f)
//                    .zIndex(Float.MAX_VALUE));
//        }
    }

    @Override
    public void finishedAddingCrimes() {
        clusterManager.cluster();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMinZoomPreference(10f);
        clusterManager = new ClusterManager<>(this, googleMap);
        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnMarkerClickListener(this);
        crimePresenter.onStart();

    }

    @Override
    public void onCameraIdle() {
        clusterManager.onCameraIdle();
        LatLngBounds latLngBounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
        crimePresenter
                .mapBoundsChanged(latLngBounds.southwest.latitude, latLngBounds.southwest.longitude,
                        latLngBounds.northeast.latitude, latLngBounds.northeast.longitude);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return clusterManager.onMarkerClick(marker);
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (googleMap != null && hasLocationPersmission()) {
            googleMap.setMyLocationEnabled(true);
        }
    }
}
