package io.github.aoemerson.crimesmvp.application;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.aoemerson.crimesmvp.model.PoliceClient;
import io.github.aoemerson.crimesmvp.model.PoliceClientImpl;
import io.github.aoemerson.crimesmvp.model.PoliceRestClient;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

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
    public PoliceRestClient providePoliceRestClient(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(PoliceRestClient.BASE_URL)
                .build()
                .create(PoliceRestClient.class);
    }

    @Provides
    @Singleton
    public OkHttpClient providesOkHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @Singleton
    public PoliceClient providePoliceClient(PoliceRestClient policeRestClient) {
        return new PoliceClientImpl(policeRestClient);
    }


}
