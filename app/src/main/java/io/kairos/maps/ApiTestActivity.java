package io.kairos.maps;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.GeocodingResult;

public class ApiTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_test);

        runServiceApiTests();
    }

    private void runServiceApiTests() {
        GeoApiContext context = new GeoApiContext();
        context.setApiKey("AIzaSyAf5SCAtNJmw1aUJknQhPzXaoUKdOokruY");
        StringBuilder sb = new StringBuilder();
        try {
            DirectionsRoute[] routes =
                    DirectionsApi.newRequest(context).origin("5000+Forbes+Avenue")
                            .destination("5822+Beacon+Street")
                            .await();

            for (DirectionsLeg leg : routes[0].legs) {
                sb.append(leg.startAddress);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("LOG_CHK: ", sb.toString());

        TextView apiTestTextView = (TextView)findViewById(R.id.apiTestTextView);
        apiTestTextView.setText(sb.toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.api_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
