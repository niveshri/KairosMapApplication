package io.kairos.maps.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.kairos.maps.R;
import io.kairos.maps.apps.KairosApp;

public class AppAdapter extends ArrayAdapter<KairosApp> {
    public AppAdapter(Context context, List<KairosApp> kairosApps) {
        super(context, R.layout.app_list_item, kairosApps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        KairosApp app = getItem(position);

        convertView = app.getAppDeckView(getContext(), parent);

        return convertView;
    }
}
