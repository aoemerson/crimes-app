package io.github.aoemerson.crimesmvp.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;


public abstract class BaseActivity extends AppCompatActivity implements CrimesView {

    private static final int LOCATION_PERM_REQ_CODE = 1;
    private LocationPermissionRequestCallback locationRequestCallback;

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
    public boolean hasLocationPersmission() {
        return ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
