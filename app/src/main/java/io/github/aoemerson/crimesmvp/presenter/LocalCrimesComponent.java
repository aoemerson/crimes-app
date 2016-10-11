package io.github.aoemerson.crimesmvp.presenter;

import java.util.List;

import javax.inject.Singleton;

import dagger.Component;
import io.github.aoemerson.crimesmvp.application.ApplicationModule;
import io.github.aoemerson.crimesmvp.model.location.CurrentLocationProviderModule;
import io.github.aoemerson.crimesmvp.view.ListCrimesActivity;


@Singleton
@Component(modules = {ApplicationModule.class, CurrentLocationProviderModule.class})
public interface LocalCrimesComponent {

    void inject(ListCrimesActivity activity);
}
