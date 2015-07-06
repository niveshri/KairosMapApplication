package io.kairos.maps.ui;

import android.content.Context;
import android.widget.Toast;

public class Notifier {
    public static void notify(Context context, String info) {
        Toast toast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
        toast.setDuration(200);
        toast.show();
        // Use text-to-speech to speak info out.
    }
}
