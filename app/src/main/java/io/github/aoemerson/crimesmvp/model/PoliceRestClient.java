package io.github.aoemerson.crimesmvp.model;

import java.util.List;

import io.github.aoemerson.crimesmvp.model.data.Crime;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Andrew on 16/08/2016.
 */
public interface PoliceRestClient {

    @GET("crimes-street/all-crime")
    Call<List<Crime>> getCrimesByPoint(@Query("lat") double lat, @Query("lng") double lng);

    class Creator {

        private static final String BASE_URL = "";

        public static PoliceRestClient newCrimesClient() {
            return new Retrofit.Builder()
                    .client(new OkHttpClient())
                    .addConverterFactory(JacksonConverterFactory.create())
                    .baseUrl("https://data.police.uk/api/")
                    .build().create(PoliceRestClient.class);
        }
    }
}
