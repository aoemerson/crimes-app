package io.github.aoemerson.crimesmvp.application;

import javax.inject.Singleton;

import dagger.Component;
import io.github.aoemerson.crimesmvp.view.ListCrimesActivity;

/**
 * Created by Andrew on 26/08/2016.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface ApplicationComponent {

    void inject(ListCrimesActivity activity);
}
