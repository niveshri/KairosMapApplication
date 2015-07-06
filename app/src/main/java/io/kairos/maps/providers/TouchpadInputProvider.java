package io.kairos.maps.providers;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TouchpadInputProvider implements KairosStreamingListener {
    private static final String TAG = TouchpadInputProvider.class.toString();

    private List<TouchpadInputListener> touchpadInputListeners;

    private static TouchpadInputProvider instance;

    private TouchpadInputProvider() {
        this.touchpadInputListeners = new ArrayList<TouchpadInputListener>();

        KairosStreamingServer.instance().requestStream(KairosStreamingServer.SWIPE_KEY, this);
    }

    public static TouchpadInputProvider instance() {
        if (instance != null) return instance;

        instance = new TouchpadInputProvider();
        return instance;
    }

    public void requestInputControls(TouchpadInputListener touchpadInputListener) {
        this.touchpadInputListeners.add(touchpadInputListener);
    }

    @Override
    public void onStringReceived(String s) {
        TouchpadInputType inputType;
        try {
            inputType = Enum.valueOf(TouchpadInputType.class, s.toUpperCase());
        } catch (Exception e) {
            Log.e(TAG, "Exception while receiving input event.", e);
            return;
        }

        for (TouchpadInputListener listener : touchpadInputListeners) {
            listener.onInputReceived(inputType);
        }
    }
}
