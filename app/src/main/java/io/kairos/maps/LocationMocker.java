package io.kairos.maps;

import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.LatLng;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.kairos.maps.math.GeometryUtils;

/**
 * Can be used to mock locations through location services. Has the following 2 interfaces
 *   1. Mock a single/list of locations.
 *   2. On being provided a route, can mock the movement through the entire route
 *
 * TODO: Refactor to use RouteLocationGenerator for location generation.
 */
public class LocationMocker implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {
    private String TAG = LocationMocker.class.toString();

    private static LocationClient locationClient;
    private static Boolean mocking = false;
    private static boolean isConnected = false;

    private static long mockLocationInterval = 1000l;
    private static final String PROVIDER = "KairosMockLocationProvider";
    public static int t = 0;
    public static final int deltaT = 4;
    private static LatLng[] latLngTMinusDeltaT = new LatLng[deltaT];

    static class LockObject {
        Object object;
        int priority;

        LockObject(Object object, int priority) {
            this.object = object;
            this.priority = priority;
        }
    }

    private static Stack<LockObject> locks = new Stack<LockObject>();

    public boolean acquireLock(Object object, int priority) {
//        synchronized (locks) {
            if (!locks.empty() && locks.peek().priority >= priority) return false;

            locks.push(new LockObject(object, priority));
            return true;
//        }
    }

    public boolean hasLock(Object object) {
//        synchronized (locks) {
            return (locks.peek().object == object);
//        }
    }

    public void releaseLock(Object object) {
//        synchronized (locks) {
            if (locks.peek().object != object) {
//            throw new RuntimeException("Trying to release lock that was never held.");
                return;
            }

            locks.pop();
//        }
    }

    static {
        LocationMocker locationMocker = new LocationMocker();
        locationClient = new LocationClient(KairosApplication.getAppContext(),
                locationMocker, locationMocker);
        locationClient.connect();
    }

    private synchronized boolean checkAndSetMockingStatus() {
        if (mocking) {
            Log.i(TAG, "LocationMocker already mocking a route.");
            return false;
        }

        mocking = true;
        return true;
    }

    public void mockLocation(LatLng latLng1, LatLng latLng2) {
        latLngTMinusDeltaT[t%deltaT] = latLng1;
        if(t<deltaT){
            locationClient.setMockLocation(map(latLng1, latLng2));
        }
        else{
            float bearing = (float)GeometryUtils.initialBearing(latLngTMinusDeltaT[(t+1)%deltaT],latLng1);
            locationClient.setMockLocation(map(latLng1, bearing));
        }
        t++;
    }

    public boolean beginMockRoute(final LocationClient locationClient,
                                  final DirectionsStep[] steps, final Object lockObject) {
        Log.d(TAG, "Mock Route Requested");

        if (!isConnected) return false;
//        if (!checkAndSetMockingStatus()) return false;
        if (steps == null || steps.length == 0) return false;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mockRoute(locationClient, steps);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Thread Interrupted Exception while mocking location.", e);
                } catch (Exception e) {
                    Log.e(TAG, "Exception while mocking location.", e);
                } finally {
//                    mocking = false;
                    releaseLock(lockObject);
                }
            }
        });

        return true;
    }

    private void mockRoute(LocationClient locationClient, DirectionsStep[] steps)
            throws InterruptedException {
        Log.d(TAG, "Mocking route on background thread.");

        try {
            locationClient.setMockMode(true);

            Log.d(TAG, "Mocking location as : " + steps[0].startLocation);
            for (int i = 0; i < steps.length; i++) {
                DirectionsStep step = steps[i];
                List<LatLng> path = step.polyline.decodePath();
                for (int j = 0; j < path.size() - 1; j++) {
                    LatLng latLng1 = path.get(j);
                    LatLng latLng2 = path.get(j + 1);
                    // TODO: interpolate between latLng1 and latLng2
                    Log.d(TAG, "Mocking location as : " + latLng1);
                    locationClient.setMockLocation(map(latLng1, latLng2));
                    Thread.currentThread().sleep(mockLocationInterval);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in mockRoute.", e);
        } finally {
//            locationClient.setMockMode(false);
        }
    }

    private Location map(LatLng latLng1, LatLng latLng2) {
        Location location = new Location(PROVIDER);
        location.setLatitude(latLng1.lat);
        location.setLongitude(latLng1.lng);
        location.setAccuracy(3.0f);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        location.setBearing((float)GeometryUtils.initialBearing(latLng1, latLng2));
        location.setSpeed(13.4f);
        location.setAltitude(0d);
        return location;
    }

    private Location map(LatLng latLng1, float bearing) {
        Location location = new Location(PROVIDER);
        location.setLatitude(latLng1.lat);
        location.setLongitude(latLng1.lng);
        location.setAccuracy(3.0f);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        location.setBearing(bearing);
        location.setSpeed(13.4f);
        location.setAltitude(0d);
        return location;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "LocationMocker Connection Failed.");
        isConnected = false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "LocationMocker Connected.");
        isConnected = true;
    }

    @Override
    public void onDisconnected() {
        Log.i(TAG, "LocationMocker Disconnected.");
        isConnected = false;
    }

    public void setMocking(boolean status){
        locationClient.setMockMode(status);
        mocking = status;
    }

    public boolean getMockingStatus(){
        return mocking;
    }

    public boolean isConnected(){
        return isConnected;
    }

}
