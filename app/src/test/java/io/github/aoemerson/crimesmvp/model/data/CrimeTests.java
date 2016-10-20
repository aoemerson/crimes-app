package io.github.aoemerson.crimesmvp.model.data;

import org.junit.Test;

import io.github.aoemerson.crimesmvp.util.CrimeTestFactory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CrimeTests {

    @Test
    public void shouldCreateCopy() {
        Crime crime1 = CrimeTestFactory.createCrimes(1).get(0);
        Crime crime2 = new Crime(crime1);
        assertThat(crime1, is(equalTo(crime2)));
    }
}
