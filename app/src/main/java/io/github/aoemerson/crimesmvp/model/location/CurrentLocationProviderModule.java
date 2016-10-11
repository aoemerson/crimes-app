package io.github.aoemerson.crimesmvp.model.location;

import android.content.Context;
import android.util.TypedValue;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import javax.inject.Named;
import javax.inject.Singleton;

import aoemeron.github.io.crimesmvp.R;
import dagger.Module;
import dagger.Provides;

@Module
public class CurrentLocationProviderModule {


    public CurrentLocationProviderModule() {


    }


    @Provides
    @Singleton
    CurrentLocationProvider providesCurrentLocationProvider(GoogleApiClient apiClient, @Named("lat") double defaultLat,  @Named("lng") double defaultLng) {
        return new GoogleFusedLocationProvider(apiClient, defaultLat, defaultLng);
    }

    @Provides
    @Named("lat")
    Double providesDefaultLattitude(Context context) {
        TypedValue outValue = new TypedValue();
        context.getResources().getValue(R.dimen.default_latitude, outValue,true);
        return  (double) outValue.getFloat();
    }

    @Provides
    @Named("lng")
    Double providesDefaultLongitude(Context context) {
        TypedValue outValue = new TypedValue();
        context.getResources().getValue(R.dimen.default_longitude, outValue, true);
        return (double) outValue.getFloat();
    }

    @Provides
    @Singleton
    GoogleApiClient providesGoogleApiClient(Context context) {

        return new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
//                .addConnectionCallbacks(connectionCallbacks)
//                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
    }
}
