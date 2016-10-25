package io.github.aoemerson.crimesmvp.model;

import java.util.List;
import java.util.Locale;

import io.github.aoemerson.crimesmvp.model.data.Crime;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Andrew on 16/08/2016.
 */
public interface PoliceRestClient {


    class RectangleBounds {

        private final double southWestLat;
        private final double southWestLng;
        private final double northEastLat;
        private final double northEastLng;
        private final double southEastLat;
        private final double southEastLng;
        private final double northWestLat;
        private final double northWestLng;

        RectangleBounds(double southWestLat, double southWestLng, double northEastLat,
                        double northEastLng) {

            this.southWestLat = southWestLat;
            this.southWestLng = southWestLng;
            this.northEastLat = northEastLat;
            this.northEastLng = northEastLng;
            this.southEastLat = southWestLat;
            this.southEastLng = northEastLng;
            this.northWestLat = northEastLat;
            this.northWestLng = southWestLng;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(southWestLat);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(southWestLng);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(northEastLat);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(northEastLng);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(southEastLat);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(southEastLng);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(northWestLat);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(northWestLng);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RectangleBounds that = (RectangleBounds) o;

            if (Double.compare(that.southWestLat, southWestLat) != 0) return false;
            if (Double.compare(that.southWestLng, southWestLng) != 0) return false;
            if (Double.compare(that.northEastLat, northEastLat) != 0) return false;
            if (Double.compare(that.northEastLng, northEastLng) != 0) return false;
            if (Double.compare(that.southEastLat, southEastLat) != 0) return false;
            if (Double.compare(that.southEastLng, southEastLng) != 0) return false;
            if (Double.compare(that.northWestLat, northWestLat) != 0) return false;
            return Double.compare(that.northWestLng, northWestLng) == 0;

        }

        @Override
        public String toString() {
            return String.format(Locale.UK, "%f,%f:%f,%f:%f,%f:%f,%f",
                    southWestLat, southWestLng, northWestLat, northWestLng,
                    northEastLat, northEastLng, southEastLat, southEastLng);
        }
    }
    String BASE_URL = "https://data.police.uk/api/";

    @GET("crimes-street/all-crime")
    Call<List<Crime>> getCrimesByPoint(@Query("lat") double lat, @Query("lng") double lng);

    @GET("crimes-street/all-crime")
    Call<List<Crime>> getCrimesByRectangle(@Query("poly") RectangleBounds bounds);
}
