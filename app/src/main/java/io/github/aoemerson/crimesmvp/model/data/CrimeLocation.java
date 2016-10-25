package io.github.aoemerson.crimesmvp.model.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Andrew on 16/08/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrimeLocation {

    private double latitude;
    private double longitude;
    private CrimeStreet street;

    public CrimeLocation() {
    }


    CrimeLocation(double lat, double lng, String street, int streetId) {
        this.latitude = lat;
        this.longitude = lng;
        this.street = new CrimeStreet(streetId, street);

    }

    public CrimeLocation(CrimeLocation location) {
        this.latitude = location.latitude;
        this.longitude = location.longitude;
        this.street = location.street != null ? new CrimeStreet(location.street) : null;

    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public CrimeStreet getStreet() {
        return street;
    }

    public void setStreet(CrimeStreet street) {
        this.street = street;
    }

    public double getLatitude() {

        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (street != null ? street.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrimeLocation that = (CrimeLocation) o;

        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;
        return street != null ? street.equals(that.street) : that.street == null;

    }

    @Override
    public String toString() {
        return "CrimeLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", street=" + street +
                '}';
    }
}
