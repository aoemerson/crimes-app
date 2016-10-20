package io.github.aoemerson.crimesmvp.application;

import android.content.Context;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.aoemerson.crimesmvp.model.PoliceClient;
import io.github.aoemerson.crimesmvp.model.PoliceClientImpl;
import io.github.aoemerson.crimesmvp.model.PoliceRestClient;
import io.github.aoemerson.crimesmvp.model.data.CrimeTranslator;
import io.github.aoemerson.crimesmvp.model.data.DefaultCrimeTranslator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import timber.log.Timber;

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

        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        long t1 = System.nanoTime();
                        Request request = chain.request();
                        Timber.v("%s %s%n%s", request.method(), request.url(), request.headers());

                        Response response = chain.proceed(request);
                        long t2 = System.nanoTime();
                        Timber.v("%d %s [+%.1fms]%n%s", response.code(), response.request()
                                                                                 .url(), (t2 - t1) / 1e6d,
                                response.headers());
                        return response;
                    }
                })
                .build();
    }

    @Provides
    @Singleton
    public PoliceClient providePoliceClient(PoliceRestClient policeRestClient) {
        return new PoliceClientImpl(policeRestClient);
    }

    @Provides
    @Singleton
    public CrimeTranslator provideCrimeTranslator(Context context) {
        return new DefaultCrimeTranslator(context);
    }

}
