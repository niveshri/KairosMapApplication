package io.kairos.maps.context;

public interface CarContextListener {
    public void onCarStateReceived(CarState carState);
    public void onCarStateChanged(CarState carState);
    public void onCarStateChangedWithThreshold(CarState carState);
}
