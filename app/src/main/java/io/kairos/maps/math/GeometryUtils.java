package io.kairos.maps.math;

import com.google.maps.model.LatLng;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import java.util.List;

import static io.kairos.maps.math.GeomMapper.map;

public class GeometryUtils {
    private static final double R = 6371 * 1000;

    public static LatLng findClosestPointOnLine(LatLng point, LatLng startLine,
                                                LatLng endLine) {
        LineString line = new GeometryFactory().createLineString(
                new Coordinate[]{map(startLine), map(endLine)});
        Point p = new GeometryFactory().createPoint(map(point));

        DistanceOp distOp = new DistanceOp(line, p);

        Coordinate[] closestPt = distOp.nearestPoints();

        return new LatLng(closestPt[0].x, closestPt[0].y);
    }

    public static double distancePointToLine(LatLng point, LatLng startLine, LatLng endLine) {
        return CGAlgorithms.distancePointLine(map(point), map(startLine), map(endLine));
    }


    /**
     * <link>http://www.movable-type.co.uk/scripts/latlong.html</link>
     *
     * The wonderful person at the link above has beautifully explained how to calculate distance
     * and angle between to geographical points in latitude and longitude. I'm but humbly
     * implementing the code.
     *
     * @param latLng1
     * @param latLng2
     * @return
     */
    public static double initialBearing(LatLng latLng1, LatLng latLng2) {
        double lat1R = Math.toRadians(latLng1.lat);
        double lat2R = Math.toRadians(latLng2.lat);
        double delLngR = Math.toRadians(latLng2.lng - latLng1.lng);

        double y = Math.sin(delLngR) * Math.cos(lat2R);
        double x = Math.cos(lat1R) * Math.sin(lat2R) -
                Math.sin(lat1R) * Math.cos(lat2R) * Math.cos(delLngR);

        double bearing = Math.toDegrees(Math.atan2(y, x));
        return bearing;
    }

    public static double crossTrackDistance(LatLng thirdPoint, LatLng startPoint, LatLng endPoint) {
        double d13 = angularDistance(startPoint, thirdPoint);
        double t13 = initialBearing(startPoint, thirdPoint);
        double t12 = initialBearing(startPoint, endPoint);

        return Math.asin(Math.sin(d13 / R) * Math.sin(t13 - t12)) * R;
    }

    public static double angularDistance(LatLng latLng1, LatLng latLng2) {
        double lat1R = Math.toRadians(latLng1.lat);
        double lat2R = Math.toRadians(latLng2.lat);
        double delLatR = Math.toRadians(latLng2.lat - latLng1.lat);
        double delLngR = Math.toRadians(latLng2.lng - latLng1.lng);

        double a = Math.sin(delLatR / 2.0) * Math.sin(delLatR / 2.0) +
                Math.cos(lat1R) * Math.cos(lat2R) * Math.sin(delLngR / 2.0) * Math.sin(delLngR / 2.0);

        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));

        return c;
    }

    public static double distanceBetween(LatLng latLng1, LatLng latLng2) {
        return R * angularDistance(latLng1, latLng2);
    }

    /**
     * Finds the next LatLng at step distance from start in the direction of end.
     *
     * @param start
     * @param end
     * @param step
     * @return
     */
    public static LatLng findNextLatLng(LatLng start, LatLng end, double step) {
        return null;
//        LineString line = new GeometryFactory().createLineString(
//                new Coordinate[]{map(start), map(end)});
    }

    /**
     * TODO: Use Location.distanceTo() which does full geodesic calculation.
     *
     * @param closestPoint
     * @param polyLine
     * @param polylineIndex
     * @return
     */
    public static double calculateRouteDistance(LatLng closestPoint,
                                                List<LatLng> polyLine, int polylineIndex) {
        double distance = map(closestPoint).distance(map((polyLine.get(polylineIndex + 1))));

        for (int i = polylineIndex + 1; i < polyLine.size() - 1; i++) {
            distance += map(polyLine.get(i)).distance(map(polyLine.get(i + 1)));
        }

        return distance;
    }
}
