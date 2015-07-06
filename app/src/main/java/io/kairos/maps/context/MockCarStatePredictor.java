package io.kairos.maps.context;

import java.util.LinkedHashMap;

/**
 * Mock car state predictions by generating random values.
 */
public class MockCarStatePredictor implements CarStatePredictor {
    private String[] states = new String[]{
            "stopped", "accelerating", "accelerating", "accelerating", "accelerating",
            "accelerating", "accelerating", "accelerating", "accelerating",
            "cruising_low_speed", "cruising_low_speed", "cruising_low_speed", "cruising_low_speed",
            "cruising_high_speed", "cruising_high_speed", "cruising_high_speed", "cruising_high_speed",
            "cruising_high_speed", "cruising_high_speed", "cruising_high_speed", "cruising_high_speed",
            "de_accelerating", "de_accelerating", "de_accelerating", "de_accelerating",
            "de_accelerating", "de_accelerating", "de_accelerating", "de_accelerating",
            "stopped", "stopped", "stopped", "stopped", "stopped",
            "stopped", "stopped", "stopped", "stopped"
    };

    private int counter = -1;

    @Override
    public CarState predict(LinkedHashMap<String, Double> values) {
        counter = (counter + 1) % states.length;
        return CarState.valueOf(states[counter].toUpperCase());
    }
}
