package io.github.aoemerson.crimesmvp.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class FileUtils {


    public static String loadResourceAsString(String resourceName) throws IOException {
        return IOUtils.toString(System.out.getClass().getResourceAsStream("/" + resourceName));
    }
}
