package io.github.aoemerson.crimesmvp.model.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Andrew on 16/08/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrimeStreet {

    private int id;
    private String name;

    public CrimeStreet() {
    }

    CrimeStreet(int streetId, String street) {
        this.id = streetId;
        this.name = street;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrimeStreet that = (CrimeStreet) o;

        if (id != that.id) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public String toString() {
        return "CrimeStreet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
