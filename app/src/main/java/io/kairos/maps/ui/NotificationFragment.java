package io.kairos.maps.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import io.kairos.maps.R;
import io.kairos.maps.apps.calling.CallingApp;
import io.kairos.maps.apps.texting.TextingApp;
import io.kairos.maps.context.CarContextProvider;
import io.kairos.maps.context.DriverLoad;
import io.kairos.maps.context.DriverLoadListener;
import io.kairos.maps.notifications.NotificationListener;
import io.kairos.maps.notifications.NotificationService;

public class NotificationFragment extends Fragment
        implements DriverLoadListener, NotificationListener {

    private NotificationService notificationService;
    private boolean notifiableState = false;

    public interface NotificationsActivatedListener {
        void onNotificationsActivated();
    }

    private NotificationsActivatedListener notificationsActivatedListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notificationService = NotificationService.instance();
    }

    @Override
    public void onStart() {
        super.onStart();

        CarContextProvider.instance().requestDriverLoad(this);
        NotificationService.instance().requestNotifications(this);

        displayNotifications(getView());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            notificationsActivatedListener = (NotificationsActivatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NotificationsActivatedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationsActivatedListener.onNotificationsActivated();
            }
        });

        return view;
    }

    private void displayNotifications(View view) {
        // Potentially do this only if there have been new notifications since the last time.
        // Can be deduced from onNotificationReceived.

        if (view == null) return;

        Map<String, Integer> notificationCounts = notificationService.getNotificationCounts();

        ImageView callsImageView = (ImageView) view.findViewById(R.id.callsNotificationImageView);
        TextView callsTextView = (TextView) view.findViewById(R.id.callsNotificationTextView);
        if (!notificationCounts.containsKey(CallingApp.CALLING_APP_ID)) {
            callsImageView.setVisibility(View.GONE);
            callsTextView.setVisibility(View.GONE);
        } else {
            callsImageView.setVisibility(View.VISIBLE);
            callsTextView.setVisibility(View.VISIBLE);
            callsTextView.setText(notificationCounts.get(CallingApp.CALLING_APP_ID).toString());
        }

        ImageView textsImageView = (ImageView) view.findViewById(R.id.textNotificationImageView);
        TextView textsTextView = (TextView) view.findViewById(R.id.textNotificationTextView);
        if (!notificationCounts.containsKey(TextingApp.TEXTING_APP_ID)) {
            textsImageView.setVisibility(View.GONE);
            textsTextView.setVisibility(View.GONE);
        } else {
            textsImageView.setVisibility(View.VISIBLE);
            textsTextView.setVisibility(View.VISIBLE);
            textsTextView.setText(notificationCounts.get(TextingApp.TEXTING_APP_ID).toString());
        }

        // Potentially notify with a beep. Notifier.
    }

    @Override
    public void onDriverLoadReceived(DriverLoad driverLoad) {
    }

    @Override
    public void onDriverLoadChanged(DriverLoad driverLoad) {
    }

    @Override
    public void onDriverLoadChangedWithThreshold(DriverLoad driverLoad) {
        if (driverLoad == DriverLoad.HIGH) {
            notifiableState = false;
            return;
        }

        notifiableState = true;
        displayNotificationsOnMainThread();
    }

    @Override
    public void onNotificationReceived(Notification notification) {
        if (notifiableState) displayNotificationsOnMainThread();
    }

    private void displayNotificationsOnMainThread() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                displayNotifications(getView());
            }
        });
    }
}
