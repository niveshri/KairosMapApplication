package io.kairos.maps.ui;

public interface AppStateChangedListener {
    void onAppStateChanged(String key, String oldValue, String newValue);
}
