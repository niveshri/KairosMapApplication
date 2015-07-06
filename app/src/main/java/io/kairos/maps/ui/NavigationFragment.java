package io.kairos.maps.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;

import java.util.HashMap;
import java.util.Map;

import io.kairos.maps.LocationMocker;
import io.kairos.maps.R;
import io.kairos.maps.navigation.NavigationException;
import io.kairos.maps.navigation.NavigationInfo;
import io.kairos.maps.navigation.Navigator;
import io.kairos.maps.perf.PerformanceCounter;

/**
 * Responsible for generating the augmented maps projection for the driver of the car.
 */
public class NavigationFragment extends Fragment implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    private String TAG = NavigationFragment.class.toString();

    public static final int ZOOM_LEVEL = 13;
    private final long LOCATION_REQUEST_INTERVAL = 500l;
    private final long LOCATION_REQUEST_FASTEST_INTERVAL = 500l;

    private LocationClient locationClient;
    private LocationRequest locationRequest;

    private NavState navState;
    private Navigator navigator;
    private GoogleMap birdsEyeMap;

    private Bitmap STEP_BITMAP;

    private Map<String, Bitmap> maneuverBitmaps;

    /**
     * Google hasn't documented the maneuvers yet. The list of maneuvers with their images
     * can be found in res/drawable/nav_icons_ref.jpg
     */
    private String[] maneuvers = new String[]{
            "turn-sharp-left", "", "uturn-right", "turn-slight-right", "", "", "", "", "merge",
            "", "", "roundabout-left", "", "roundabout-right", "", "", "", "uturn-left", "", "", "",
            "turn-slight-left", "", "turn-left", "ramp-right", "", "", "turn-right", "fork-right",
            "", "straight", "fork-left", "ferry-train", "turn-sharp-right", "ramp-left", "ferry",
            "keep-left", "keep-right"};

    private Integer[] maneuversImages = new Integer[]{
            R.drawable.turn_sharp_left, null, R.drawable.uturn_right, R.drawable.turn_slight_right,
            null, null, null, null, R.drawable.merge, null, null, -1, null, -1, null, null, null,
            R.drawable.uturn_left, null, null, null, R.drawable.turn_slight_left, null,
            R.drawable.turn_left, -2, null, null, R.drawable.turn_right, -1, null, R.drawable.straight,
            -1, -1, R.drawable.turn_slight_right, -1, -1, -1, -1};

    @Override
    public void onStart() {
        super.onStart();

        setHasOptionsMenu(false);

        locationClient = new LocationClient(getActivity(), this, this);
        locationClient.connect();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL);

        setUpMapIfNeeded();
        setNavState(NavState.PLAIN);

        STEP_BITMAP = BitmapFactory.decodeResource(getResources(), R.drawable.nav_icons);

        maneuverBitmaps = new HashMap<String, Bitmap>();
        for (int i = 0; i < maneuvers.length; i++) {
            Integer image = maneuversImages[i];
            if (image == null || image < 0)
                continue;

            maneuverBitmaps.put(maneuvers[i],
                    BitmapFactory.decodeResource(getResources(), maneuversImages[i]));
        }

        AppState.instance().remove("routing");
        AppState.instance().requestAppStateChanges(new AppStateChangedListener() {
            @Override
            public void onAppStateChanged(String key, String oldValue, String newValue) {
                if (!key.equalsIgnoreCase("routing")) return;

                if (newValue == null) setNavState(NavState.PLAIN);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    public void mockRoute() {
        Log.d(TAG, "Mock Route Button Clicked");
        if (navState != NavState.ROUTING) return;

        LocationMocker locationMocker = new LocationMocker();
        while (!locationMocker.acquireLock(this, 10))
            ;;

        locationMocker.beginMockRoute(locationClient, navigator.getSteps(), this);
    }

    // Create mini-google maps for current location.
    private void setUpMapIfNeeded() {
        if (birdsEyeMap == null) {
            birdsEyeMap = ((SupportMapFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.birdsEyeMap)).getMap();

            if (birdsEyeMap != null) {
                birdsEyeMap.setMyLocationEnabled(true);
                birdsEyeMap.setTrafficEnabled(true);
                birdsEyeMap.animateCamera(CameraUpdateFactory.zoomTo(50.0f));
//                birdsEyeMap.getUiSettings().setAllGesturesEnabled(false);
                birdsEyeMap.getUiSettings().setZoomControlsEnabled(false);
                birdsEyeMap.getUiSettings().setMyLocationButtonEnabled(false);
                birdsEyeMap.getUiSettings().setCompassEnabled(true);

                // TODO: Make bird's eye map transparent.
                // In the long run this should be custom drawn since the Google Maps fragment
                // has too much detail. A simpler version needed.
//        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.birdsEyeMap))
//                  .getView().setAlpha(0.7f).setBackgroundResource(android.R.color.transparent);

            }
        }
    }

    private com.google.android.gms.maps.model.LatLng map(Location location) {
        return new com.google.android.gms.maps.model.LatLng(
                location.getLatitude(), location.getLongitude());
    }

    public void route(String destination) {
        Log.i(TAG, "Routing to destination - " + destination);

        if (destination == "") return;

        if (!locationClient.isConnected()) {
            Notifier.notify(getActivity(), "Not connected. Can't receive location.");
            return;
        }

        Location lastLocation = locationClient.getLastLocation();

        Log.i(TAG, String.format("Fetching route from %s to %s.",
                lastLocation.toString(), destination));

        try {
            navigator = new Navigator(lastLocation, destination);
            navigator.route();

            Notifier.notify(getActivity(), "Routing...");
            setNavState(NavState.ROUTING);
            AppState.instance().set("routing", destination);

            drawRoutingView(navigator.getNavigationInfo());
        } catch (NavigationException e) {
            Log.e(TAG, "Error routing to destination.", e);
            Notifier.notify(getActivity(), "Error routing to destination.");
        }
    }

    private void setNavState(NavState navState) {
        View layoutNavigate = getView().findViewById(R.id.layoutNavigate);

        switch (navState) {
            case PLAIN:
                navigator = null;
                layoutNavigate.setVisibility(View.INVISIBLE);
                break;
            case ROUTING:
                layoutNavigate.setVisibility(View.VISIBLE);
                break;
        }

        this.navState = navState;
    }

    @Override
    public void onStop() {
        locationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected to Location Services.");
        Notifier.notify(getActivity(), "Connected. Can receive location.");
        locationClient.requestLocationUpdates(locationRequest, this);
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "Disconnected Location Services.");
        Notifier.notify(getActivity(), "Oops! Connection failed. Can't receive location.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed." + connectionResult.toString());
        Notifier.notify(getActivity(), "Oops! Connection failed. Can't receive location.");
    }

    @Override
    public void onLocationChanged(Location currentLocation) {
        Log.d(TAG, "Location changed: " + currentLocation);

        // update contextMapView

        // update location in the birds eye map.
        updateLocationOnBirdsEyeMap(currentLocation);

        if (navState != NavState.ROUTING) return;
        PerformanceCounter updateCounter = PerformanceCounter.get("navigator_updatelocation");
        updateCounter.start();
//        navigator.updateLocation(currentLocation);
        navigator.updateLocationBruteForce(currentLocation);
        updateCounter.stop();
        Log.i(TAG, "Update location Perf counter : " + updateCounter);

        PerformanceCounter drawCounter = PerformanceCounter.get("navigator_draw");
        drawCounter.start();
        NavigationInfo navigationInfo = navigator.getNavigationInfo();
        drawRoutingView(navigationInfo);
        drawCounter.stop();
        Log.i(TAG, "Drawing Location Perf counter : " + drawCounter);
    }

    private void drawRoutingView(NavigationInfo navigationInfo) {
        TextView nextStepDistanceView = (TextView) getView().findViewById(R.id.nextStepDistanceTextView);
        nextStepDistanceView.setText(nextStepDistanceFormat(navigationInfo.getDistanceToNextStep()));

        drawNextStepArrow(navigationInfo);

        TextView remainingDistanceTextView = (TextView) getView().findViewById(R.id.remainingDistanceTextView);
        remainingDistanceTextView.setText(totalDistanceFormat(navigationInfo.getRemainingDistance()));

        TextView remainingTimeTextView = (TextView) getView().findViewById(R.id.remainingTimeTextView);
        remainingTimeTextView.setText(timeFormat(navigationInfo.getRemainingTime()));
    }

    private void drawNextStepArrow(NavigationInfo navigationInfo) {
        Bitmap nextStepBitmap = maneuverBitmaps.get(navigationInfo.getNextManeuver());
        if (nextStepBitmap == null) return;

//        int startY = navigationInfo.getNextManeuver().contains("right") ? 28
//                : navigationInfo.getNextManeuver().contains("left") ? 24 : 31;
//
//        Bitmap nextStepBitmap = Bitmap.createBitmap(
//                STEP_BITMAP, 0, (startY - 1) * STEP_BITMAP.getHeight() / 36,
//                STEP_BITMAP.getWidth(), STEP_BITMAP.getHeight() / 36, null, false);

        ImageView nextStepImageView = (ImageView) getView().findViewById(R.id.nextStepImageView);
        nextStepImageView.setImageBitmap(nextStepBitmap);
    }

    private String timeFormat(double remainingTime) {
        int mins = (int)remainingTime / 60;
        int hrs = mins / 60;
        mins = mins % 60;

        if (hrs == 0) return String.format("%d min", mins);

        return String.format("%dh %dm", hrs, mins);
    }

    private String nextStepDistanceFormat(double distance) {
        if (distance < 100) return String.format("%.0fm", distance);

        return String.format("%.1fkm", distance / 1000);
    }

    private String totalDistanceFormat(double distance) {
        if (distance < 100) return String.format("%.0fm", distance);

        return String.format("%.1fkm", distance / 1000);
    }

    private void updateLocationOnBirdsEyeMap(Location currentLocation) {
        com.google.android.gms.maps.model.LatLng location = map(currentLocation);
        birdsEyeMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                CameraPosition.builder().target(location).zoom(ZOOM_LEVEL).build()));
    }

    public void onBackPressed() {
        switch (navState) {
            case ROUTING:
                setNavState(NavState.PLAIN);
                break;
        }
    }
}
