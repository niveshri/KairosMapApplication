package io.kairos.maps;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MockLocationActivity extends Activity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private TextView displayTextView;

    private LocationClient locationClient;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_location);

        displayTextView = (TextView) findViewById(R.id.displayTextView);

        locationClient = new LocationClient(this, this, this);
        locationClient.connect();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000l);
        locationRequest.setFastestInterval(1000l);
    }

    @Override
    public void onConnected(Bundle bundle) {
        displayText("Connected.");
        locationClient.requestLocationUpdates(locationRequest, this);
    }

    @Override
    public void onDisconnected() {
        displayText("Disconnected.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        displayText("Connection Failed.");
    }

    private Location getLocation(LatLng latLng) {
        Location location = new Location("flp");
        location.setLatitude(latLng.latitude);
        location.setLatitude(latLng.longitude);
        location.setAccuracy(3.0f);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        location.setSpeed(13.4f);
        location.setBearing(0f);
        location.setAltitude(0d);
        return location;
    }

    public void onMockLocationButtonClicked(View view) {
        displayText("Started Mocking Location");
        locationClient.setMockMode(true);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                LatLng latLng = new LatLng(10.0, 20.0);
                for (int i = 0; i < 5; i++) {
                    Log.i("mock", "Mocking location ");
                    locationClient.setMockLocation(getLocation(add(latLng, i)));
                    try {
                        Thread.sleep(1000l);
                    } catch (InterruptedException e) {
                        displayText("interrupted exception.");
                        return;
                    }
                }

                locationClient.setMockMode(false);
            }

            private LatLng add(LatLng latLng, int i) {
                return new LatLng(latLng.latitude + i, latLng.longitude + i);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        displayText("Location Changed - " + location.toString());
    }

    public void displayText(String displayText) {
        displayText = displayTextView.getText() + NEW_LINE + displayText;
        if (displayText.length() > 500)
            displayText = displayText.substring(displayText.length() - 500);

        displayTextView.setText(displayText);
    }
}
