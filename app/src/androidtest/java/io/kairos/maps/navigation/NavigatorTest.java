package io.kairos.maps.navigation;

import android.location.Location;
import android.os.SystemClock;
import android.util.Log;

import com.google.maps.model.LatLng;

import junit.framework.TestCase;

public class NavigatorTest extends TestCase {
    private LatLng cmuLatLng = new LatLng(40.4410627, -79.9423122);
    private String destinationAddress = "5822+Beacon+Street";
    private Location cmuLocation;

    @Override
    protected void setUp() throws Exception {
        cmuLocation = new Location("test");
        cmuLocation.setLatitude(cmuLatLng.lat);
        cmuLocation.setLongitude(cmuLatLng.lng);
        cmuLocation.setAccuracy(3.0f);
        cmuLocation.setTime(System.currentTimeMillis());
        cmuLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        cmuLocation.setSpeed(13.4f);
        cmuLocation.setBearing(0f);
        cmuLocation.setAltitude(0d);
    }

    public void testRoute() throws Exception {
        Navigator navigator = new Navigator(cmuLocation, destinationAddress);
        navigator.route();

        RouteLocationGenerator routeLocationGenerator = new RouteLocationGenerator(navigator.getSteps());
        for (Location location : routeLocationGenerator) {
            navigator.updateLocation(location);
            NavigationInfo navigationInfo = navigator.getNavigationInfo();
            Log.i("NavigatorTest", navigationInfo.toString());
        }
    }
}
