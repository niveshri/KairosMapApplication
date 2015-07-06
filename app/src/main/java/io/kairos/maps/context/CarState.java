package io.kairos.maps.context;

/**
 * Encapsulates the current state of the vehicle. Also let's apps know whether it is a state in
 * which drivers may be notified.
 */
public enum CarState {
        /*
        OFF,
        STOPPED_OUT_OF_TRAFFIC,
        STOPPED_SIGNAL,
        STOPPED_AWAITING_ACTION,
        TURNING,
        ACCELERATING,
        DE_ACCELERATING,
        REVERSE,
        LANE_CHANGING,
        CRUISING_OVER_SPEED,
        CRUISING_HIGH_SPEED,
        CRUISING_LOW_SPEED,
        CRUISING_VERY_SLOW,
        PARKING
        */

        ACCELERATING,
        LANE_CHANGING,
        DE_ACCELERATING,
        CRUISING_HIGH_SPEED,
        CRUISING_MEDIUM_SPEED,
        REVERSE,
        CRUISING_LOW_SPEED,
        STOPPED,
        TURNING


}
