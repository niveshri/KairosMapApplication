package io.kairos.maps.apps.navigation;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;

import io.kairos.maps.R;
import io.kairos.maps.apps.KairosApp;
import io.kairos.maps.ui.AppState;
import io.kairos.maps.ui.Notification;

public class NavigationApp implements KairosApp {
    @Override
    public String getName() {
        return "Navigation";
    }

    @Override
    public Fragment getAppMainFragment() {
        return new NavigationAppFragment();
    }

    @Override
    public View getAppDeckView(Context context, ViewGroup parent) {
        View appDeckView = LayoutInflater.from(context).inflate(
                R.layout.navigation_app_tile, parent, false);

        TextView navigationContent = (TextView) appDeckView.findViewById(R.id.navName);
        TextView location = (TextView) appDeckView.findViewById(R.id.location);

        String routing = AppState.instance().get("routing");
        navigationContent.setText((routing != null) ? routing : "");
        location.setText((routing != null) ? "Destination" : "Off");

        return appDeckView;
    }

    @Override
    public View getNotificationView(Context context, ViewGroup parent, Notification notification) {
        return null;
    }
}
