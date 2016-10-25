package io.github.aoemerson.crimesmvp.util;

import timber.log.Timber;

public class TestLogging {

    public static void initTimber() {
        Timber.plant(new Timber.Tree() {
            @Override
            protected void log(int priority, String tag, String message, Throwable t) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("%s: %s", tag, message));
                if (t != null) {
                    sb.append("\n");
                    sb.append(t.getMessage());
                    if (t.getStackTrace() != null) {
                        for (StackTraceElement element : t.getStackTrace()) {
                            sb.append(element.toString());
                            sb.append("\n");
                        }
                    }
                }
                System.out.println(sb.toString());
            }
        });
    }
}
