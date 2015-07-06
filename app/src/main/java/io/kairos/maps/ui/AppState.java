package io.kairos.maps.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates the current state of the app that various different UI pieces can query from.
 */
public class AppState {
    private Map<String, String> appStateMap = new HashMap<String, String>();
    private List<AppStateChangedListener> listeners = new ArrayList<AppStateChangedListener>();

    private static AppState instance = new AppState();

    public static AppState instance() {
        return instance;
    }

    private AppState() {}

    public void set(String key, String value) {
        String oldValue = appStateMap.get(key);

        appStateMap.put(key, value);

        if (oldValue != value) {
            for (AppStateChangedListener appStateChangedListener : listeners) {
                appStateChangedListener.onAppStateChanged(key, oldValue, value);
            }
        }
    }

    public String get(String key) {
        return appStateMap.get(key);
    }

    public boolean contains(String key) {
        return appStateMap.containsKey(key);
    }

    public String remove(String key) {
        if (!appStateMap.containsKey(key)) return null;

        String value = appStateMap.remove(key);
        for (AppStateChangedListener appStateChangedListener : listeners) {
            appStateChangedListener.onAppStateChanged(key, value, null);
        }

        return value;
    }

    public void requestAppStateChanges(AppStateChangedListener appStateChangedListener) {
        if (!this.listeners.contains(appStateChangedListener))
            this.listeners.add(appStateChangedListener);
    }
}
