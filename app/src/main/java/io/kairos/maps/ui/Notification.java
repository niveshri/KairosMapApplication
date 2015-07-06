package io.kairos.maps.ui;

/**
 * Represents a notification sent to the notification fragment to be displayed.
 */
public class Notification {
    private String key;
    private Object value;

    public Notification(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
