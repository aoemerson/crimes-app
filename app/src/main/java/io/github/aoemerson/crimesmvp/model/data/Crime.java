package io.github.aoemerson.crimesmvp.model.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Andrew on 16/08/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Crime {

    public static class Builder {

        private Crime crime;

        public Builder() {
            this.crime = new Crime();
        }

        public Builder id(long id) {
            crime.id = id;
            return this;
        }

        public Builder category(String category) {
            crime.category = category;
            return this;
        }

        public Builder location(double latitude, double longitude, String street, int streetId) {
            crime.location = new CrimeLocation(latitude, longitude, street, streetId);
            return this;
        }

        public Builder monthString(String monthString) {
            crime.monthString = monthString;
            return this;
        }

        public Crime build() {
            return crime;
        }
    }
    private String category;
    private CrimeLocation location;
    @JsonProperty("month")
    private String monthString;
    private long id;

    public Crime() {

    }

    Crime(long id, String category, String monthString, double lat, double lng, String street, int streetId) {
        this.id = id;
        this.category = category;
        this.location = new CrimeLocation(lat, lng, street, streetId);
        this.monthString = monthString;
    }

    public Crime(Crime crime) {
        this.id = crime.id;
        this.category = crime.category;

        this.location = crime.location != null ? new CrimeLocation(crime.location) : null;
        this.monthString = crime.monthString;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public CrimeLocation getLocation() {
        return location;
    }

    public void setLocation(CrimeLocation location) {
        this.location = location;
    }

    public String getMonthString() {
        return monthString;
    }

    public void setMonthString(String monthString) {
        this.monthString = monthString;
    }

    @Override
    public int hashCode() {
        int result = category.hashCode();
        result = 31 * result + location.hashCode();
        result = 31 * result + monthString.hashCode();
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Crime crime = (Crime) o;

        if (id != crime.id) return false;
        if (!category.equals(crime.category)) return false;
        if (!location.equals(crime.location)) return false;
        return monthString.equals(crime.monthString);

    }

    @Override
    public String toString() {
        return "Crime{" +
                "category='" + category + '\'' +
                ", location=" + location +
                ", monthString='" + monthString + '\'' +
                ", id=" + id +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
