package io.kairos.maps;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;


public class UIExperimentActivity extends Activity {

    private Bitmap STEP_BITMAP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uiexperiment);

        STEP_BITMAP = BitmapFactory.decodeResource(getResources(), R.drawable.nav_icons);
        applyImage(getValue());

        ArrayList<String> items = new ArrayList<String>();
        items.add("Item 1");
        items.add("Item 2");
        items.add("Item 3");
        items.add("Item 4");
        items.add("Item 5");
        items.add("Item 6");
        items.add("Item 7");
        items.add("Item 8");

        ArrayAdapter<String> aItems = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        TwoWayView lvTest = (TwoWayView) findViewById(R.id.lvItems);
        lvTest.setAdapter(aItems);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.uiexperiment, menu);
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

    private int getValue() {
        EditText arrowNumber = (EditText) findViewById(R.id.arrowNumber);
        String str = arrowNumber.getText().toString();
        return Integer.parseInt(str);
    }

    private void applyImage(int num) {
        Bitmap nextStepBitmap = Bitmap.createBitmap(
                STEP_BITMAP, 0, (num - 1) * STEP_BITMAP.getHeight() / 36,
                STEP_BITMAP.getWidth(), STEP_BITMAP.getHeight() / 36, null, false);

        ImageView arrowView = (ImageView) findViewById(R.id.arrowView);
        arrowView.setImageBitmap(nextStepBitmap);
    }

    public void onButtonClick(View view) {
        Toast.makeText(this, "Clicked!", Toast.LENGTH_LONG);
        applyImage(getValue());
    }
}
