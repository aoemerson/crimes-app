package io.github.aoemerson.crimesmvp.presenter;

import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.aoemerson.crimesmvp.view.CrimesView;

@Module
public class CrimeListPresenterModule {

    private CrimesView view;

    public CrimeListPresenterModule(CrimesView view) {
        this.view = view;
    }

    @Provides
    @Singleton
    CrimesView providesCrimesView() {
        return view;
    }

}
