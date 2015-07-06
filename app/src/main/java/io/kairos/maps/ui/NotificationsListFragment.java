package io.kairos.maps.ui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

import io.kairos.maps.notifications.NotificationService;

/**
 * Fragment that displays the list of notifications.
 *
 * For each notification, fetch it's display view from the supporting app. Instantiate it
 * based on the current notification and display it.
 *
 * Swiping a notification removes it. Clicking it will have an action defined by the app.
 *
 * Currently in case a new notification arrives when this fragment is displayed, it won't update
 * the new notification.
 */
public class NotificationsListFragment extends ListFragment {
    private NotificationsAdapter notificationsAdapter;
    private NotificationService notificationService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        notificationService = NotificationService.instance();

        notificationsAdapter = new NotificationsAdapter(
                getActivity(), notificationService.getNotifications());
        setListAdapter(notificationsAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Change text property of notifications to listItem
        setEmptyText("No Notifications!");
    }
}
