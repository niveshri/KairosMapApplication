package io.kairos.maps.context;

/**
 * Listener interface for the driver's cognitive load.
 */
public interface DriverLoadListener {
    /**
     * Sends a call back every time the driver's load is perceived in the system. At every sampling
     * of sensor data, the system predicts driver load and invokes the driver load with the current
     * state.
     *
     * @param driverLoad
     */
    void onDriverLoadReceived(DriverLoad driverLoad);

    /**
     * Call back invoked every time the driver load changes from the previous state, ie. from every
     * transition of the driver's load from LOW -> HIGH or HIGH -> LOW.
     *
     * @param driverLoad
     */
    void onDriverLoadChanged(DriverLoad driverLoad);

    /**
     * Similar to above but slightly more intelligent system which gets invoked immediately if the
     * driver state changes to LOW, but waits for a THRESHOLD number of times before it switches to
     * HIGH.
     *
     * @param driverLoad
     */
    void onDriverLoadChangedWithThreshold(DriverLoad driverLoad);
}
