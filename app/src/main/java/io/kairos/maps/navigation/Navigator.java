package io.kairos.maps.navigation;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.LatLng;

import java.util.List;

import io.kairos.maps.KairosApplication;
import io.kairos.maps.R;
import io.kairos.maps.math.GeometryUtils;

import static io.kairos.maps.math.GeomMapper.map;

/**
 * Responsible for navigating a device from an origin to a destination, by fetching a path
 * via Google Maps service. Provides next actions/information based on current location and
 * detects when a device goes out of path.
 *
 * Edge Cases
 * 1. What if driver moves out of route. When to re-route
 * 2. What if driver is taking a U-Turn. How do we detect which leg of the route they
 * are in?
 * 3. To be able to change the u-turn/turn arrow as the car is turning
 * 4. What if there is a fault in the location and it jumps out and back in.
 *
 * Detecting a driver has gone out of route. Current Location doesn't fall on either current
 * step or next step.
 *
 */
public class Navigator {
    private static final String TAG = Navigator.class.toString();
    // Within specified distance of line
    private static final double LINE_DISTANCE_THRESHOLD = 100;

    private GeoApiContext geoApiContext;

    private Location origin;
    private String destination;

    private Location currentLocation;
    private DirectionsRoute[] routes;
    private DirectionsStep[] steps;
    private List<LatLng> currentPolyline;
    private int currentStepIndex;
    private int currentPolylineIndex;

    public Navigator(Location origin, String destination) {
        Context appContext = KairosApplication.getAppContext();
        geoApiContext = new GeoApiContext();
        geoApiContext.setApiKey(appContext.getString(R.string.google_maps_key));

        this.origin = origin;
        this.destination = destination;
        this.currentLocation = origin;
    }

    public DirectionsStep[] getSteps() {
        return steps;
    }

    private String getString(DirectionsRoute[] routes) {
        StringBuilder sb = new StringBuilder();
        for (DirectionsStep step : routes[0].legs[0].steps) {
            sb.append(String.format("Start: %s, End: %s", step.startLocation.toString(),
                    step.endLocation.toString()));
        }
        return sb.toString();
    }

    public void route() throws NavigationException {
        Log.i(TAG, "Routing the navigator. Fetching route.");
        try {
            routes = DirectionsApi.newRequest(geoApiContext)
                    .origin(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .destination(destination)
                    .await();
            steps = routes[0].legs[0].steps;

            currentStepIndex = 0;
            currentPolylineIndex = 0;
            currentPolyline = steps[0].polyline.decodePath();

            updateLocation(currentLocation);

        } catch (Exception e) {
            Log.e(TAG, "Error while routing to " + destination, e);
            throw new NavigationException(e);
        }
    }

    public void updateLocationBruteForce(Location location) {
        Log.i(TAG, "Updating location brute force.");
        LatLng loc = map(location);

        List<LatLng> tempPolyLine = null;
        double minDistance = Double.MAX_VALUE;
        int minStepIndex = -1; int minPolyIndex = -1;

        double distance;
        for (int i = 0; i < steps.length; i++) {
            tempPolyLine = steps[i].polyline.decodePath();
            for (int j = 0; j < tempPolyLine.size(); j++) {
                distance = Math.abs(GeometryUtils.distanceBetween(loc, tempPolyLine.get(j)));
                if (distance < minDistance) {
                    minDistance = distance;
                    minPolyIndex = (j == tempPolyLine.size() - 1) ? j - 1 : j;
                    minStepIndex = i;
                }
            }
        }

        if (minDistance > LINE_DISTANCE_THRESHOLD) {
            // Outside of entire route.
            // Probably not the best idea since it happens on main UI thread.
            try {
                route();
            } catch (NavigationException e) {
                e.printStackTrace();
            }
        }

        currentStepIndex = minStepIndex;
        currentPolylineIndex = minPolyIndex;
        currentPolyline = steps[currentStepIndex].polyline.decodePath();
    }

    // Possibly update location via listener rather than an explicit call.
    public void updateLocation(Location location) {
        Log.i(TAG, "Updating location.");
        LatLng loc = map(location);

        // Check all the line segments of current step and the first line segment of the next step.
        // Choose the line segment closest to the current location if found within
        // minimum distance of those, simply choose it.

        List<LatLng> tempPolyLine = null;
        double minDistance = Double.MAX_VALUE;
        int minStepIndex = currentStepIndex; int minPolyIndex = -1;

        double distance;
        for (int i = currentPolylineIndex; i < currentPolyline.size() - 1; i++) {
            distance = GeometryUtils.crossTrackDistance(loc,
                    currentPolyline.get(currentPolylineIndex),
                    currentPolyline.get(currentPolylineIndex + 1));

            if (distance < minDistance) {
                minDistance = distance;
                minPolyIndex = i;
            }
        }
        if (currentStepIndex < steps.length - 1) {
            tempPolyLine = steps[currentStepIndex + 1].polyline.decodePath();
            distance = GeometryUtils.crossTrackDistance(loc,
                    tempPolyLine.get(0), tempPolyLine.get(1));
            if (distance < minDistance) {
                minDistance = distance;
                minPolyIndex = 0;
                minStepIndex = currentStepIndex + 1;
            }
        }

        if (minDistance < LINE_DISTANCE_THRESHOLD) {
            // Found current step and index with minimum effort.
            currentPolylineIndex = minPolyIndex;
            if (minStepIndex != currentStepIndex) {
                currentStepIndex = minStepIndex;
                currentPolyline = tempPolyLine;
            }
            currentLocation = location;
            return;
        }


        // If line segment is not found within the current step, then continue over all steps
        // until a closest line segment is found within THRESHOLD. This ensures that the correct
        // segment is found even when the car has moved multiple segments/steps between a location
        // update.

        for (int i = currentStepIndex + 1; i < steps.length; i++) {
            tempPolyLine = steps[i].polyline.decodePath();
            for (int j = 0; j < tempPolyLine.size() - 1; j++) {
                distance = GeometryUtils.crossTrackDistance(loc,
                        tempPolyLine.get(j), tempPolyLine.get(j + 1));
                if (distance < LINE_DISTANCE_THRESHOLD) {
                    currentStepIndex = i;
                    currentPolylineIndex = j;
                    currentPolyline = tempPolyLine;
                    currentLocation = location;
                    return;
                }
            }
        }

        // TODO: Current Location not part of the route. Should re-route.
        try {
            route();
        } catch (NavigationException e) {
            e.printStackTrace();
        }
    }

    private String getNextManeuver() {
        return steps[currentStepIndex].maneuver;
    }

    public NavigationInfo getNavigationInfo() {
        Log.i(TAG, "Fetching navigation info");
        String nextManeuver = getNextManeuver();
        if (nextManeuver == null) nextManeuver = "straight";

        double distanceToNextStep = getDistanceToNextStep();
        double remainingStepsDistance = getFutureStepsRemainingDistance();
        double remainingDistance = distanceToNextStep + remainingStepsDistance;

        double currentStepTime = distanceToNextStep / steps[currentStepIndex].distance.inMeters
                * steps[currentStepIndex].duration.inSeconds;
        double remainingTime = currentStepTime + getFutureStepsRemainingTime();

        return new NavigationInfo(nextManeuver, distanceToNextStep,
                remainingDistance, remainingTime);
     }

    private double getDistanceToNextStep() {
        double distanceToNextStep = 0d;

        // Find the closest point on the current line segment from the current location.
        // Calculate the distance from this to the end of the line segment.
        // TODO: Currently closest point assumes a flat surface. This should be moved to spherical
        // geodesic math.
//        LatLng closestPoint = GeometryUtils.findClosestPointOnLine(map(currentLocation),
//                currentPolyline.get(currentPolylineIndex),
//                currentPolyline.get(currentPolylineIndex + 1));
//        distanceToNextStep += Math.abs(GeometryUtils.distanceBetween(closestPoint,
//                currentPolyline.get(currentPolylineIndex + 1)));

        // Deduce the distance of the rest of the step by accumulating line segments.
        for (int i = currentPolylineIndex; i < currentPolyline.size() - 1; i++) {
            distanceToNextStep += Math.abs(GeometryUtils.distanceBetween(
                    currentPolyline.get(i), currentPolyline.get(i + 1)));
        }
        return distanceToNextStep;
    }

    private double getFutureStepsRemainingDistance() {
        double remainingStepsDistance = 0d;
        for (int i = currentStepIndex + 1; i < steps.length; i++) {
            remainingStepsDistance += steps[i].distance.inMeters;
        }
        return remainingStepsDistance;
    }

    private double getFutureStepsRemainingTime() {
        double remainingStepTime = 0d;
        for (int i = currentStepIndex + 1; i < steps.length; i++) {
            remainingStepTime += steps[i].duration.inSeconds;
        }
        return remainingStepTime;
    }

}
