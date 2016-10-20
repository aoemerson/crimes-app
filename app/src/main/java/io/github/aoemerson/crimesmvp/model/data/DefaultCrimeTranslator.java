package io.github.aoemerson.crimesmvp.model.data;

import android.content.Context;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.inject.Inject;

import aoemerson.github.io.crimesmvp.R;
import timber.log.Timber;

public class DefaultCrimeTranslator implements CrimeTranslator {


    private HashMap<String, String> categoryMap = null;

    DefaultCrimeTranslator() {

    }

    @Inject
    public DefaultCrimeTranslator(Context context) {
        final InputStream inputStream = context.getResources()
                                               .openRawResource(R.raw.categories_map);
        new Thread(new Runnable() {
            @Override
            public void run() {
                readCategoryFile(inputStream);
            }
        }).start();
    }

    synchronized void readCategoryFile(InputStream inputStream) {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType javaType = objectMapper.getTypeFactory()
                                        .constructMapType(HashMap.class, String.class, String.class);

        try {
            categoryMap = objectMapper
                    .readValue(inputStream, javaType);
        } catch (IOException e) {
            Timber.e(e, "Unable to read category mapping file");
            categoryMap = null;
        }
    }

    @Override
    public Crime translate(Crime crime) {
        Crime translated = new Crime(crime);
        if (categoryMap != null) {
            String mapped = categoryMap.get(crime.getCategory());
            if (mapped != null && mapped.length() > 0)
                translated.setCategory(mapped);
        }
        return translated;
    }


}
