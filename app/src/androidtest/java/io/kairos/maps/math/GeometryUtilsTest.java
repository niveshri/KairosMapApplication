package io.kairos.maps.math;

import android.util.Log;

import com.google.maps.model.LatLng;

import junit.framework.TestCase;

public class GeometryUtilsTest extends TestCase {
    private final String TAG = GeometryUtilsTest.class.toString();

    public void testFindClosestPointOnLine() throws Exception {
//        Vector2 point = GeometryUtils.findClosestPointOnLine(
//                new Vector2(0, 0), new Vector2(1, 0), new Vector2(1, 1));
//
//        Log.i(TAG, "LOG_CHK: " + point);
    }

    public void testDistanceBetween() {
        LatLng latLng1 = new LatLng(40.4410048, -79.9423087);
        LatLng latLng2 = new LatLng(40.4457651, -79.9367297);

        double distance = GeometryUtils.distanceBetween(latLng1, latLng2);
        Log.i(TAG, "Distance is : " + distance);
    }

}