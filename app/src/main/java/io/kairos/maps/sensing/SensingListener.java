package io.kairos.maps.sensing;

import java.util.LinkedHashMap;

public interface SensingListener {
    void onSense(LinkedHashMap<String, Double> values);
}
