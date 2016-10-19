package io.github.aoemerson.crimesmvp.model;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.net.SocketTimeoutException;
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
            if (response.isSuccessful()) {
                listener.onCrimesLoadComplete(response.body());
            } else {
                int code = response.code();
                if (code >= 400 && code < 500) {
                    if (code == 429) {
                        listener.onTooManyRequests();
                    } else {
                        handleUserError(response);
                    }
                } else if (code >= 500 && code < 600) {
                    handleServerError(response);
                } else {
                    handleOtherError(response);
                }
            }
        }

        @Override
        public void onFailure(Call<List<Crime>> call, Throwable t) {
            if (t instanceof SocketTimeoutException) {
                listener.onReadTimeOut(t);
            } else {
                listener.onCrimesLoadError(t);
            }
        }

        private void handleOtherError(Response<List<Crime>> response) {
            try {
                listener.onOtherError(getReason(response));
            } catch (IOException e) {
                listener.onOtherError(getAltReason(response));
            }
        }

        private void handleUserError(Response<List<Crime>> response) {
            try {
                listener.onUserError(getReason(response));
            } catch (IOException e) {
                listener.onUserError(getAltReason(response));
            }
        }

        @NonNull
        private String getAltReason(Response<List<Crime>> response) {
            return response.code() + ": <could not read server error response body>";
        }

        @NonNull
        private String getReason(Response<List<Crime>> response) throws IOException {
            return response.code() + ": " + response.errorBody().string();
        }

        private void handleServerError(Response<List<Crime>> response) {
            try {
                listener.onServerError(getReason(response));
            } catch (IOException e) {
                listener.onServerError(getAltReason(response));
            }
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
        policeRestClient.getCrimesByPoint(lat, lng).enqueue(new RestCallback(listener));
    }

    @Override
    public void requestCrimesByRectangularBounds(double southWestLat, double southWestLng, double northEastLat, double northEastLng, OnCrimesLoadedListener listener) {
        policeRestClient
                .getCrimesByRectangle(new PoliceRestClient.RectangleBounds(southWestLat, southWestLng, northEastLat, northEastLng))
                .enqueue(new RestCallback(listener));
    }


}
