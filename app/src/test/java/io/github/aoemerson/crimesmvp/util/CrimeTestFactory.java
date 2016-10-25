package io.github.aoemerson.crimesmvp.util;

import java.util.ArrayList;
import java.util.List;

import io.github.aoemerson.crimesmvp.model.data.Crime;

public class CrimeTestFactory {

    public static long crimeId = 0;

    public static List<Crime> createCrimes(int howMany) {
        ArrayList<Crime> crimes = new ArrayList<>(howMany);
        for (int i = 0; i < howMany; i++) {
            Crime crime = new Crime.Builder()
                    .category("Cat" + 1)
                    .id(++crimeId)
                    .location(12d, 11d, "street", i)
                    .monthString("06-2016")
                    .build();
            crimes.add(crime);
        }
        return crimes;
    }
}
