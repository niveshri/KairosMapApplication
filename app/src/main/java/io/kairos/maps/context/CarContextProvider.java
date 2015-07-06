package io.kairos.maps.context;

import android.util.Log;

import org.apache.http.client.protocol.RequestAddCookies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import io.kairos.maps.sensing.SensingListener;

public class CarContextProvider implements SensingListener {
    private final String TAG = CarContextProvider.class.toString();

    private static final List<CarState> HIGH_COGNITIVE_LOAD_STATES = Arrays.asList(
            //CarState.STOPPED_AWAITING_ACTION,
            CarState.TURNING,
            CarState.ACCELERATING,
            CarState.DE_ACCELERATING,
            CarState.LANE_CHANGING,
            //CarState.CRUISING_OVER_SPEED,
            //CarState.CRUISING_VERY_SLOW,
            //CarState.PARKING,
            CarState.REVERSE
    );

    /**
     * Minimum number of times a particular state has to be observed before it's observers are
     * notified.
     */
    private static final int MIN_DRIVER_STATE_THRESHOLD = 3;
    private static final int MIN_CAR_STATE_THRESHOLD = 3;

    private CarState carState;
    private int currentCarStateCount = 0;
    private int currentDriverStateCount = 0;

    private CarStatePredictor carStatePredictor;
    private List<CarContextListener> carContextListeners;

    private List<DriverLoadListener> driverLoadListeners;

    private static CarContextProvider instance;
    public static CarContextProvider instance() {
        if (instance == null) {
            instance = new CarContextProvider(new MockCarStatePredictor());
//            instance = new CarContextProvider(new HttpCarStatePredictor());
        }

        return instance;
    }

    // TODO: Change back to private.
    public CarContextProvider(CarStatePredictor carStatePredictor) {
        this.carStatePredictor = carStatePredictor;

        this.carContextListeners = new ArrayList<CarContextListener>();
        this.driverLoadListeners = new ArrayList<DriverLoadListener>();
    }

    public void requestCarStates(CarContextListener carContextListener) {
        if (!carContextListeners.contains(carContextListener))
            carContextListeners.add(carContextListener);
    }

    public void requestDriverLoad(DriverLoadListener driverLoadListener) {
        if (!driverLoadListeners.contains(driverLoadListener))
            driverLoadListeners.add(driverLoadListener);
    }

    public CarState lastCarState() {
        return carState;
    }

    public DriverLoad lastDriverLoad() {
        return driverLoadFromCarState(carState);
    }

    public DriverLoad driverLoadFromCarState(CarState carState) {
        if (HIGH_COGNITIVE_LOAD_STATES.contains(carState)) return DriverLoad.HIGH;
        if (carState == null) return DriverLoad.HIGH;

        return DriverLoad.LOW;
    }

    @Override
    public synchronized void onSense(LinkedHashMap<String, Double> values) {
        CarState predictedCarState;
        try {
            predictedCarState = carStatePredictor.predict(values);
        } catch (Exception e) {
            Log.e(TAG, "Error while predicting car state.", e);
            return;
        }

        DriverLoad predictedDriverLoad = driverLoadFromCarState(predictedCarState);

        boolean carStateChanged = predictedCarState != carState;
        boolean driverLoadChanged = predictedDriverLoad != driverLoadFromCarState(carState);

        currentDriverStateCount = driverLoadChanged ? 1 : currentDriverStateCount + 1;
        currentCarStateCount = carStateChanged ? 1 : currentCarStateCount + 1;

        carState = predictedCarState;

        boolean carStateReset = false;
        boolean driverLoadReset = false;

        for (CarContextListener carContextListener : carContextListeners) {
            carContextListener.onCarStateReceived(carState);
            if (carStateChanged) carContextListener.onCarStateChanged(carState);

            if (currentCarStateCount >= MIN_CAR_STATE_THRESHOLD) {
                carStateReset = true;
                carContextListener.onCarStateChangedWithThreshold(carState);
            }
        }
        if (carStateReset) currentCarStateCount = 0;

        for (DriverLoadListener driverLoadListener : driverLoadListeners) {
            driverLoadListener.onDriverLoadReceived(predictedDriverLoad);
            if (driverLoadChanged) driverLoadListener.onDriverLoadChanged(predictedDriverLoad);

            if (driverLoadChanged && predictedDriverLoad == DriverLoad.HIGH ||
                predictedDriverLoad == DriverLoad.LOW && currentDriverStateCount >= MIN_DRIVER_STATE_THRESHOLD) {
                driverLoadReset = true;
                driverLoadListener.onDriverLoadChangedWithThreshold(predictedDriverLoad);
            }
        }
        if (driverLoadReset) currentDriverStateCount = 0;
    }
}
