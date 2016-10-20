package io.github.aoemerson.crimesmvp.model.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class CrimeClusterItem implements ClusterItem {

    private final Crime crime;

    public CrimeClusterItem(Crime crime) {
        this.crime = crime;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(crime.getLocation().getLatitude(), crime.getLocation().getLongitude());
    }

    public Crime getCrime() {
        return crime;
    }
}
