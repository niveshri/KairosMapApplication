package io.kairos.maps.context;

import java.util.LinkedHashMap;

/**
 * Make a prediction for every sample of sensor readings.
 */
public interface CarStatePredictor {
    CarState predict(LinkedHashMap<String, Double> values);
}
