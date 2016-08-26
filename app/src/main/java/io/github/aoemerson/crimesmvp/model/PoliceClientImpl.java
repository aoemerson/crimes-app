package io.github.aoemerson.crimesmvp.model;

import java.util.List;

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
            listener.onLoadComplete(response.body());
        }

        @Override
        public void onFailure(Call<List<Crime>> call, Throwable t) {
            listener.onLoadError(t);
        }
    }

    private final Retrofit retrofit;

    public PoliceClientImpl() {
        retrofit = new Retrofit.Builder()
                .client(new OkHttpClient())
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl("https://data.police.uk/api/")
                .build();
    }

    @Override
    public void requestCrimesByPoint(float lat, float lng, OnCrimesLoadedListener listener) {
        PoliceRestClient policeRestClient = retrofit.create(PoliceRestClient.class);
        policeRestClient.getCrimesByPoint(lat,lng).enqueue(new RestCallback(listener));
    }
}
