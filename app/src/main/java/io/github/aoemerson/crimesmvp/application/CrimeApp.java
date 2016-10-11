package io.github.aoemerson.crimesmvp.application;

import android.app.Application;

import aoemeron.github.io.crimesmvp.BuildConfig;
import timber.log.Timber;

/**
 * Created by Andrew on 26/08/2016.
 */
public class CrimeApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

    }


}
