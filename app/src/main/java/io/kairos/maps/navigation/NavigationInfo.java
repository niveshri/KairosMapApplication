package io.kairos.maps.navigation;

public class NavigationInfo {
    private String nextManeuver;
    private double distanceToNextStep;
    private double remainingDistance;
    private double remainingTime;

    public NavigationInfo(String nextManeuver, double distanceToNextStep,
                          double remainingDistance, double remainingTime) {
        this.nextManeuver = nextManeuver;
        this.distanceToNextStep = distanceToNextStep;
        this.remainingDistance = remainingDistance;
        this.remainingTime = remainingTime;
    }

    public String getNextManeuver() {
        return nextManeuver;
    }

    public double getDistanceToNextStep() {
        return distanceToNextStep;
    }

    public double getRemainingDistance() {
        return remainingDistance;
    }

    public double getRemainingTime() {
        return remainingTime;
    }

    @Override
    public String toString() {
        return "NavigationInfo{" +
                "remainingTime=" + remainingTime +
                ", remainingDistance=" + remainingDistance +
                ", distanceToNextStep=" + distanceToNextStep +
                ", nextManeuver='" + nextManeuver + '\'' +
                '}';
    }
}
