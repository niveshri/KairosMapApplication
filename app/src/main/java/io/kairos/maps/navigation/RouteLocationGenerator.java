package io.kairos.maps.navigation;

import android.location.Location;
import android.os.SystemClock;

import com.google.maps.model.DirectionsStep;
import com.google.maps.model.LatLng;

import java.util.Iterator;
import java.util.List;

import io.kairos.maps.math.GeometryUtils;

/**
 * Given a route provided by Google Maps, this class generates Location objects by following the
 * route.
 * TODO: Implement interpolation to generate finer route segments within a polyline fragment.
 */
public class RouteLocationGenerator implements Iterator<Location>, Iterable<Location> {
    private final DirectionsStep[] steps;
    private List<LatLng> currentPolyLine;
    private int currentStepIndex;
    private int currentPolylineIndex;

    public RouteLocationGenerator(final DirectionsStep[] steps) {
        this.steps = steps;
        this.currentPolyLine = steps[0].polyline.decodePath();
        this.currentStepIndex = 0;
        this.currentPolylineIndex = 0;
    }

    @Override
    public boolean hasNext() {
        if (currentPolylineIndex == currentPolyLine.size() &&
            currentStepIndex == steps.length - 1)
            return false;

        return true;
    }

    @Override
    public Location next() {
//        if (!hasNext()) throw new NoSuchElementException();

        if (currentPolylineIndex == currentPolyLine.size()) {
            currentStepIndex++;
            currentPolylineIndex = 0;
            currentPolyLine = steps[currentStepIndex].polyline.decodePath();
        }

        LatLng latLng1 = currentPolyLine.get(currentPolylineIndex);
        LatLng latLng2 = null;
        if (currentPolylineIndex != currentPolyLine.size() - 1) {
            latLng2 = currentPolyLine.get(currentPolylineIndex + 1);
        } else {
            if (currentStepIndex < steps.length - 1)
                latLng2 = steps[currentStepIndex + 1].polyline.decodePath().get(0);
        }

        currentPolylineIndex++;

        return convert(latLng1, latLng2);
    }

    private Location convert(LatLng latLng1, LatLng latLng2) {
        Location location = new Location("RouteLocationGenerator");
        location.setLatitude(latLng1.lat);
        location.setLongitude(latLng1.lng);
        location.setAccuracy(3.0f);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        if (latLng2 != null)
            location.setBearing((float) GeometryUtils.initialBearing(latLng1, latLng2));
        location.setSpeed(13.4f);
        location.setAltitude(0d);
        return location;
    }

    @Override
    public void remove() {
    }

    @Override
    public Iterator<Location> iterator() {
        return this;
    }
}
