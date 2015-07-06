package io.kairos.maps.notifications;

import io.kairos.maps.ui.Notification;

public interface NotificationListener {
    void onNotificationReceived(Notification notification);
}
