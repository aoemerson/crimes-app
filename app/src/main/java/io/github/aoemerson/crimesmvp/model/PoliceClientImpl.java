package io.github.aoemerson.crimesmvp.model;

import java.util.List;

import javax.inject.Inject;

import io.github.aoemerson.crimesmvp.model.data.Crime;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Andrew on 17/08/2016.
 */
public class PoliceClientImpl implements PoliceClient {

    private static class RestCallback implements Callback<List<Crime>> {

        private OnCrimesLoadedListener listener;

        public RestCallback(OnCrimesLoadedListener listener) {
            this.listener = listener;
        }

        @Override
        public void onResponse(Call<List<Crime>> call, Response<List<Crime>> response) {
            listener.onCrimesLoadComplete(response.body());
        }

        @Override
        public void onFailure(Call<List<Crime>> call, Throwable t) {
            listener.onCrimesLoadError(t);
        }
    }

    private PoliceRestClient policeRestClient;


    public PoliceClientImpl() {
        policeRestClient = new Retrofit.Builder()
                .client(new OkHttpClient())
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl("https://data.police.uk/api/")
                .build().create(PoliceRestClient.class);
    }

    @Inject
    public PoliceClientImpl(PoliceRestClient policeRestClient) {
        this.policeRestClient = policeRestClient;
    }

    @Override
    public void requestCrimesByPoint(double lat, double lng, OnCrimesLoadedListener listener) {
        policeRestClient.getCrimesByPoint(lat,lng).enqueue(new RestCallback(listener));
    }
}
