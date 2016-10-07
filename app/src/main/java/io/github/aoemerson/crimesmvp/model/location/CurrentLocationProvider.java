package io.github.aoemerson.crimesmvp.model.location;

/**
 * Created by Andrew on 17/08/2016.
 */
public interface CurrentLocationProvider {

    class MissingPermissionException extends Exception {

        private MissingPermissionException() {
            super();
        }

        public MissingPermissionException(String message) {
            super(message);
        }

        public MissingPermissionException(String message, Throwable cause) {
            super(message, cause);
        }

        public MissingPermissionException(Throwable cause) {
            super(cause);
        }
    }

    interface LocationRequestCallback {
        void onLocationObtained(double lat, double lng);
        void onLocationRequestError(Throwable e);
    }

    void requestCurrentLocation(LocationRequestCallback callback) throws GoogleFusedLocationProvider.MissingPermissionException;
}
