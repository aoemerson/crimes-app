package io.github.aoemerson.crimesmvp.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Andrew on 16/08/2016.
 */
public interface PoliceRestClient {

    @GET("crimes-street/all-crime")
    Call<List<Crime>> getCrimesByPoint(@Query("lat") float lat, @Query("lng") float lng);

}
