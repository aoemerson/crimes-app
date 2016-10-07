package io.github.aoemerson.crimesmvp.model.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Andrew on 16/08/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrimeLocation {

    private float latitude;
    private float longitude;
    private CrimeStreet street;

    public CrimeLocation() {
    }

    CrimeLocation(float lat, float lng, String street, int streetId) {
        this.latitude = lat;
        this.longitude = lng;
        this.street = new CrimeStreet(streetId, street);

    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public CrimeStreet getStreet() {
        return street;
    }

    public void setStreet(CrimeStreet street) {
        this.street = street;
    }

    public float getLatitude() {

        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    @Override
    public int hashCode() {
        int result = (latitude != +0.0f ? Float.floatToIntBits(latitude) : 0);
        result = 31 * result + (longitude != +0.0f ? Float.floatToIntBits(longitude) : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrimeLocation that = (CrimeLocation) o;

        if (latitude != that.latitude) return false;
        if (longitude != that.longitude) return false;
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
