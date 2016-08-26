package io.github.aoemerson.crimesmvp.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Andrew on 16/08/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Crime {

    private String category;
    private CrimeLocation location;
    @JsonProperty("month")
    private String monthString;

    public Crime() {

    }

    Crime(String category, String monthString, float lat, float lng, String street, int streetId) {
        this.category = category;
        this.location = new CrimeLocation(lat, lng, street, streetId);
        this.monthString = monthString;
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
        int result = category != null ? category.hashCode() : 0;
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (monthString != null ? monthString.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Crime crime = (Crime) o;

        if (category != null ? !category.equals(crime.category) : crime.category != null)
            return false;
        if (location != null ? !location.equals(crime.location) : crime.location != null)
            return false;
        return monthString != null ? monthString
                .equals(crime.monthString) : crime.monthString == null;

    }

    @Override
    public String toString() {
        return "Crime{" +
                "category='" + category + '\'' +
                ", location=" + location +
                ", monthString='" + monthString + '\'' +
                '}';
    }
}
