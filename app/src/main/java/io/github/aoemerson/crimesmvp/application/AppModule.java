package io.github.aoemerson.crimesmvp.application;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return context;
    }
}
