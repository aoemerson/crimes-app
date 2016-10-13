package io.github.aoemerson.crimesmvp.model;

import java.util.List;

import io.github.aoemerson.crimesmvp.model.data.Crime;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Andrew on 16/08/2016.
 */
public interface PoliceRestClient {


    String BASE_URL = "https://data.police.uk/api/";

    @GET("crimes-street/all-crime")
    Call<List<Crime>> getCrimesByPoint(@Query("lat") double lat, @Query("lng") double lng);
}
