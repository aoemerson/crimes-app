package io.github.aoemerson.crimesmvp.application;

import android.app.Application;

/**
 * Created by Andrew on 26/08/2016.
 */
public class CrimeApp extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder()
                                                         .appModule(new AppModule(this))
                                                         .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
