package io.kairos.maps.apps.navigation;

import io.kairos.maps.R;

/**
 * Encapsulates a location to navigate to be displayed on the app
 */
public class NavigationTarget {
    private String displayName;
    private String address;
    private int navigationIcon;

    public NavigationTarget(String displayName, String address, int navigationIcon) {
        this.displayName = displayName;
        this.address = address;
        this.navigationIcon = navigationIcon;
    }

    public NavigationTarget(String displayName, String address) {
        this.displayName = displayName;
        this.address = address;
        this.navigationIcon = R.drawable.location_green;
    }

    public String getAddress() {
        return address;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName + ": " + address.subSequence(0, Math.min(address.length(), 20));
    }

    public int getNavigationIcon() {
        return navigationIcon;
    }
}
