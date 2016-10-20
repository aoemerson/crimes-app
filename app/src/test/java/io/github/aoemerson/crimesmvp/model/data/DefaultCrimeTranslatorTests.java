package io.github.aoemerson.crimesmvp.model.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import timber.log.Timber;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCrimeTranslatorTests {

    private static String[] keys = {
            "all-crime",
            "anti-social-behaviour",
            "bicycle-theft",
            "burglary",
            "criminal-damage-arson",
            "drugs",
            "other-theft",
            "possession-of-weapons",
            "public-order",
            "robbery",
            "vehicle-crime",
            "violent-crime",
            "other-crime",
            "shoplifting",
            "theft-from-the-person"};

    private static String[] values = {
            "All crime",
            "Anti-social behaviour",
            "Bicycle theft",
            "Burglary",
            "Criminal damage and arson",
            "Drugs",
            "Other theft",
            "Possession of weapons",
            "Public order",
            "Robbery",
            "Vehicle crime",
            "Violence and sexual offences",
            "Other crime",
            "Shoplifting",
            "Theft from the person"
    };

    @Before
    public void setup() {
        Timber.plant(new Timber.Tree() {
            @Override
            protected void log(int priority, String tag, String message, Throwable t) {
                StringBuilder st = new StringBuilder();
                for (StackTraceElement element : t.getStackTrace()) {
                    st.append(element.toString());
                    st.append("\n");
                }

                System.out
                        .println(String.format("%s: %s%n%s%n%s", tag, message, t.getMessage(), st));
            }
        });
    }

    @Test
    public void shouldMapNames() {
        DefaultCrimeTranslator translator = new DefaultCrimeTranslator();
        translator.readCategoryFile(getClass().getResourceAsStream("/categories_map.json"));
        assertThat("Misconfigured test - must have same number of keys as values!", keys.length, is(values.length));
        for (int i = 0; i < keys.length; i++) {
            testTranslation(translator, keys[i], values[i]);
        }
//        testTranslation(translator, key, value);


    }

    private void testTranslation(DefaultCrimeTranslator translator, String key, String value) {
        Crime crime = new Crime();
        crime.setCategory(key);
        Crime translate = translator.translate(crime);
        assertThat(translate.getCategory(), is(value));
        assertThat(crime == translate, is(false));
    }
}
