package io.github.aoemerson.crimesmvp.application;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.aoemerson.crimesmvp.model.PoliceClient;
import io.github.aoemerson.crimesmvp.model.PoliceClientImpl;
import io.github.aoemerson.crimesmvp.model.PoliceRestClient;

@Module
public class ApplicationModule {

    private Context context;

    public ApplicationModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return context;
    }

    @Provides
    @Singleton
    public PoliceRestClient providePoliceRestClient() {
        return PoliceRestClient.Creator.newCrimesClient();
    }

    @Provides
    @Singleton
    public PoliceClient providePoliceClient(PoliceRestClient policeRestClient) {
        return new PoliceClientImpl(policeRestClient);
    }


}
