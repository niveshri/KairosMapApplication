package io.kairos.maps.ui;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import io.kairos.maps.apps.KairosApp;
import io.kairos.maps.apps.KairosAppLoader;

public class NotificationsAdapter extends ArrayAdapter<Notification> {
    private KairosAppLoader kairosAppLoader;

    public NotificationsAdapter(FragmentActivity activity, List<Notification> notifications) {
        super(activity, android.R.layout.simple_list_item_1, notifications);

        kairosAppLoader = KairosAppLoader.instance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notification notification = getItem(position);

        KairosApp app = kairosAppLoader.getKairosApp(notification.getKey());

        return app.getNotificationView(getContext(), parent, notification);
    }
}
