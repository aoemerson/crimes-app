Crimes Map
==========

This android app is a _work-in-progress_ (SPOILER: bugs :beetle::beetle::beetle:) which intends to show a map of UK crimes committed in a locality.
<div align="center">
<img src="https://aoemerson.github.io/img/crime_app_1.png" height=500/><img src="https://aoemerson.github.io/img/crime_app_2.png" height=500/></div>

The app sources information about Crimes from the [police.uk](https://www.police.uk/) crime [API](https://data.police.uk/docs/).

The goal of this project is to build a robust, useful app which follows Material Design principles and could scale to a larger team and / or user-base. As such, the focus is more on architecture, process and "testability" than on being fully-featured.

###Technologies / techniques used

* **Model-View-Presenter** architecture in order to ensure clean separation of concerns as much as possible and to facilitate as much unit-testing (without instrumentation) as possible.
* **[Dagger 2](https://google.github.io/dagger/) for dependency injection** to assist with separation of concerns, aid testability and avoid the referencing of Android `Context` objects in Presenter code.
* **[Google Maps Android API v2](https://developers.google.com/maps/documentation/android-api/)** to display crimes on the map and the [Clustering utility library](https://developers.google.com/maps/documentation/android-api/utility/marker-clustering) to prevent the map getting too cluttered with pins
* **[Butterknife](http://jakewharton.github.io/butterknife/)** for easy view binding
* **[Retrofit](https://square.github.io/retrofit/)** for type-safe querying of the Police API.
* **[Mockito](http://site.mockito.org/)** for the mocking and stubbing required for good unit tests.
* **[Mock Web Server](https://github.com/square/okhttp/tree/master/mockwebserver)** for testing API client functionality, particularly concerning issues such as timeouts, `5**`, `4**` errors, etc.
* **More to come, probably** - as mentioned, this is still WIP so some more techniques and technologies will be added as I go along.

