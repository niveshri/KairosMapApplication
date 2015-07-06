package io.kairos.maps.notifications;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.kairos.maps.ui.Notification;

/**
 * Class which manages notifications. Ability to add/remove notifications.
 *
 * Edge Case: Not thread-safe.
 */
public class NotificationService {
    private LinkedHashMap<String, Integer> notificationCounts;
    private List<Notification> notificationList;

    private NotificationListener notificationListener;

    private NotificationService() {
        notificationList = new ArrayList<Notification>();
        notificationCounts = new LinkedHashMap<String, Integer>();
    }

    private static NotificationService notificationService = new NotificationService();

    public static NotificationService instance() {
        return notificationService;
    }

    public void notify(Notification notification) {
        notificationList.add(notification);

        if (!notificationCounts.containsKey(notification.getKey()))
            notificationCounts.put(notification.getKey(), 0);
        notificationCounts.put(notification.getKey(), notificationCounts.get(notification.getKey()) + 1);

        if (notificationListener != null)
            notificationListener.onNotificationReceived(notification);
    }

    public void remove(Notification notification) {
        notificationList.remove(notification);
        notificationCounts.put(notification.getKey(), notificationCounts.get(notification.getKey()) - 1);

        if (notificationCounts.get(notification.getKey()) == 0)
            notificationCounts.remove(notification);
    }

    public Map<String, Integer> getNotificationCounts() {
        return notificationCounts;
    }

    public void requestNotifications(NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    public List<Notification> getNotifications() {
        return notificationList;
    }
}
