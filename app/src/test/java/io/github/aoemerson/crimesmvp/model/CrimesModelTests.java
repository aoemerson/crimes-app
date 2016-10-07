package io.github.aoemerson.crimesmvp.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.List;

import io.github.aoemerson.crimesmvp.model.data.Crime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants io.github.aoemerson.crimesmvp.view.
 */
@RunWith(MockitoJUnitRunner.class)
public class CrimesModelTests {


    @Mock
    PoliceClient policeClient;

    @Before
    public void setup() throws IOException {
        Answer answer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                List<Crime> crimes = deserialiseTest1Crimes();
                ((PoliceClient.OnCrimesLoadedListener) invocation.getArguments()[2]).onLoadComplete(crimes);
                return null;
            }
        };
        doAnswer(answer).when(policeClient).requestCrimesByPoint(eq(52.629729f), eq(-1.131592f), any(PoliceClient.OnCrimesLoadedListener.class));
    }

    private List<Crime> deserialiseTest1Crimes() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Crime> crimes = objectMapper.readValue(getClass()
                .getResourceAsStream("/test1Crimes.json"), new TypeReference<List<Crime>>() {});
        return crimes;
    }

    @Test
    public void canDeserialisePoliceJsonWithCallback() {
        policeClient.requestCrimesByPoint(52.629729f, -1.131592f, new PoliceClient.OnCrimesLoadedListener() {
            @Override
            public void onLoadComplete(List<Crime> crimes) {
                checkTest1Crimes(crimes);
            }

            @Override
            public void onLoadError(Throwable t) {

            }
        });
    }

    private void checkTest1Crimes(List<Crime> crimesByPoint) {
        Crime crime1 = crimesByPoint.get(0);
//        Crime expectedCrime1 = new Crime("categ", "month",0 /*lat*/,0 /*lng*/,"street", 4 /*streetId*/);
        Crime expectedCrime1 = new Crime("anti-social-behaviour", "2013-01", 52.625325f, -1.110284f, "On or near Draper Street", 882469);
        assertEquals(expectedCrime1, crime1);

        Crime crime2 = crimesByPoint.get(1);
        Crime expectedCrime2 = new Crime("anti-social-behaviour", "2013-01", 52.637161f, -1.112261f, "On or near Vulcan Road", 883042);
        assertEquals(expectedCrime2, crime2);
    }


}

