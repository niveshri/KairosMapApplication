package io.kairos.maps.navigation;

import android.location.Location;
import android.util.Log;

import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import junit.framework.TestCase;

public class RouteLocationGeneratorTest extends TestCase {
    private DirectionsStep[] steps;

    @Override
    protected void setUp() throws Exception {
        steps = new DirectionsStep[4];
        for (int i = 0; i < steps.length; i++)
            steps[i] = new DirectionsStep();

        steps[0].polyline = new EncodedPolyline("okzuFft|fNDKBCB?bAZ");
        steps[1].polyline = new EncodedPolyline("}hzuFrt|fN@OHq@@K@MHy@Di@Fg@Fm@j@_GLmAF]LO|AgBRMxBmBzCqC`CuBfA_A|@y@`@_@`CwBdD}Cr@s@bAy@nAeAHKHKFIBI@I@M?Q?M?SW{Jg@uQ");
        steps[2].polyline = new EncodedPolyline("w`yuFf{yfNn@?X?X?X?zBClEGxCAj@A~BG");
        steps[3].polyline = new EncodedPolyline("ihxuFnzyfNWqD_@{FSkC_@iFQwCSaCGq@");
    }

    public void testRouteLocationGenerator() {
        RouteLocationGenerator routeLocationGenerator = new RouteLocationGenerator(steps);

        for(Location location : routeLocationGenerator) {
            Log.i("RouteTest", location.toString());
        }
    }
}
