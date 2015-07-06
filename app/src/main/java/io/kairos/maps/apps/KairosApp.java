package io.kairos.maps.apps;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import io.kairos.maps.ui.Notification;

public interface KairosApp {
    String getName();
    Fragment getAppMainFragment();
    View getAppDeckView(Context context, ViewGroup parent);
    View getNotificationView(Context context, ViewGroup parent, Notification notification);
}
