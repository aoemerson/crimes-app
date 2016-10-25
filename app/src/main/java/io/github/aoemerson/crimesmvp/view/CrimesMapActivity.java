package io.github.aoemerson.crimesmvp.view;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import aoemerson.github.io.crimesmvp.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.aoemerson.crimesmvp.application.ApplicationModule;
import io.github.aoemerson.crimesmvp.model.data.Crime;
import io.github.aoemerson.crimesmvp.model.data.CrimeClusterItem;
import io.github.aoemerson.crimesmvp.presenter.CrimeListPresenterImpl;
import io.github.aoemerson.crimesmvp.presenter.DaggerLocalCrimesComponent;

public class CrimesMapActivity extends BaseActivity
        implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMarkerClickListener,
        ClusterManager.OnClusterClickListener<CrimeClusterItem>,
        ClusterManager.OnClusterItemClickListener<CrimeClusterItem> {


    private void setMapInNormalMode() {
        googleMap.setPadding(0, getMapTopPadding(), 0, 0);
    }

    @Inject CrimeListPresenterImpl crimePresenter;
    @BindView(R.id.crimes_list_view) RecyclerView crimesRecyclerView;
    @BindView(R.id.bottom_sheet) CardView bottomSheet;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private ClusterManager<CrimeClusterItem> clusterManager;
    private CrimesRecyclerViewAdapter crimesRecyclerViewAdapter;
    private BottomSheetBehavior<CardView> bottomSheetBehaviour;
    private CameraPosition
            previousPosition;

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
        crimesRecyclerViewAdapter = new CrimesRecyclerViewAdapter();
        crimesRecyclerView.setAdapter(crimesRecyclerViewAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        crimesRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager
                .getOrientation());
        crimesRecyclerView
                .addItemDecoration(dividerItemDecoration);
        bottomSheetBehaviour = BottomSheetBehavior
                .from(bottomSheet);
    }


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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Dismiss bottom sheet if the user touches outside it.
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (bottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                Rect bottomSheetRect = new Rect();
                bottomSheet.getGlobalVisibleRect(bottomSheetRect);
                if (!bottomSheetRect.contains(((int) ev.getRawX()), ((int) ev.getRawY()))) {
                    bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);

                }
            }
        }
        boolean b = super.dispatchTouchEvent(ev);
        return b;
    }

    @Override
    public void onBackPressed() {
        switch (bottomSheetBehaviour.getState()) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                setMapInNormalMode();
                bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
                returnToPreviousPosition();
                break;
            case BottomSheetBehavior.STATE_EXPANDED:
                crimesRecyclerView.scrollToPosition(0);
                collapseBottomSheet();
                break;
            default:
                if (!returnToPreviousPosition()) {
                    super.onBackPressed();
                }
                break;
        }
    }

    private boolean returnToPreviousPosition() {
        if (previousPosition != null) {
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(previousPosition));
            previousPosition = null;
            return true;
        } else {
            return false;
        }
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
        Toast.makeText(this, "Location permission denied - showing London instead.", Toast.LENGTH_SHORT)
             .show();
    }

    @Override
    public void showLocationUnavailableError() {
        Toast.makeText(this, "Current location unavailable - showing London instead.", Toast.LENGTH_SHORT)
             .show();

    }

    @Override
    @SuppressWarnings({"MissingPermission"}) // already checked by this stage
    public void showCurrentLocation(double lat, double lng) {
        LatLng latLng = new LatLng(lat, lng);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        if (googleMap != null && hasLocationPersmission()) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void showCrimes(List<Crime> crimes) {
        recordGoBackPosition();
        crimesRecyclerViewAdapter.setCrimes(crimes);
        if (bottomSheetBehaviour.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            setMapInCollapsedMode();
            collapseBottomSheet();
        }

        LatLngBounds crimeBounds = getCrimeBounds(crimes);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(crimeBounds,
                60);
        googleMap.animateCamera(cameraUpdate);

    }

    private void recordGoBackPosition() {
        if (previousPosition == null) {
            previousPosition = googleMap.getCameraPosition();
        }
    }

    private void setMapInCollapsedMode() {
        googleMap.setPadding(0,
                getMapTopPadding(),
                0,
                getMapBottomSheetPadding());
    }

    private int getMapBottomSheetPadding() {
        return getResources().getDimensionPixelSize(R.dimen.map_bottom_sheet_peek_height);
    }

    private int getMapTopPadding() {
        return getResources().getDimensionPixelSize(R.dimen.map_top_padding_avoid_status_bar);
    }

    private void collapseBottomSheet() {
        bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private LatLngBounds getCrimeBounds(List<Crime> crimes) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Crime crime : crimes) {
            double latitude = crime.getLocation().getLatitude();
            double longitude = crime.getLocation().getLongitude();
            builder.include(new LatLng(latitude, longitude));
        }
        return builder.build();
    }

    @Override
    public void showCrime(Crime crime) {

    }

    @Override
    public void clearCrimes() {
        clusterManager.clearItems();
    }

    @Override
    public void finishedAddingCrimes() {
        clusterManager.cluster();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        setMapInNormalMode();
        googleMap.setMinZoomPreference(10f);
        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnMarkerClickListener(this);


        clusterManager = new ClusterManager<>(this, googleMap);
        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);

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
    public boolean onClusterClick(Cluster<CrimeClusterItem> cluster) {

        Collection<CrimeClusterItem> items = cluster.getItems();
        long[] ids = new long[items.size()];
        int i = 0;
        for (CrimeClusterItem item : items) {
            ids[i++] = item.getCrime().getId();
        }
        crimePresenter.crimesGroupClicked(cluster.getPosition().latitude, cluster
                .getPosition().longitude, ids);
        return true;
    }

    @Override
    public boolean onClusterItemClick(CrimeClusterItem crimeClusterItem) {
        return false;
    }

}
