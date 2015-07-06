package io.kairos.maps.math;

import android.location.Location;

import com.google.maps.model.LatLng;
import com.vividsolutions.jts.geom.Coordinate;

public class GeomMapper {
    public static Coordinate map(LatLng latLng) {
        return new Coordinate(latLng.lat, latLng.lng);
    }

    public static LatLng map(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
