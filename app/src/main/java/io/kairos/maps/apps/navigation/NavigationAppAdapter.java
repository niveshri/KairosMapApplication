package io.kairos.maps.apps.navigation;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.kairos.maps.R;

public class NavigationAppAdapter extends ArrayAdapter<NavigationTarget> {
    private List<NavigationTarget> navigationTargets = Arrays.asList(
            new NavigationTarget("Home", "5822 Beacon Street", R.drawable.home_green),
            new NavigationTarget("Work", "5000 Forbes Avenue", R.drawable.work_green)
    );

    public NavigationAppAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1, new ArrayList<NavigationTarget>());
        populateNavigationAdapter();
    }

    private void populateNavigationAdapter() {
        for (NavigationTarget navigationTarget : navigationTargets) {
            this.add(navigationTarget);
        }
    }

    public void filter(List<String> textList) {
        List<NavigationTarget> navigationTargetsToRemove = new ArrayList<NavigationTarget>();

        if (textList != null || textList.size() == 0) return;
        String text = textList.get(0);
        if (text != null || text.trim().equalsIgnoreCase("")) return;

        for (int i = 0; i < this.getCount(); i++) {
            NavigationTarget navigationTarget = getItem(i);
            if (navigationTarget.getAddress().toLowerCase().startsWith(text.toLowerCase()) ||
                navigationTarget.getDisplayName().toLowerCase().startsWith(text.toLowerCase()))
                continue;

            navigationTargetsToRemove.add(navigationTarget);
        }

        for (NavigationTarget navigationTarget : navigationTargetsToRemove) {
            remove(navigationTarget);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NavigationTarget navigationTarget = getItem(position);

        convertView = LayoutInflater.from(getContext())
                .inflate(R.layout.navigation_app_list_item, parent, false);

        TextView navigationName = (TextView) convertView.findViewById(R.id.navigationNameTextView);
        navigationName.setText(navigationTarget.getDisplayName());

        TextView navigationAddress = (TextView) convertView.findViewById(
                R.id.navigationAddressTextView);
        navigationAddress.setText(navigationTarget.getAddress());

        ImageView navigationIcon = (ImageView) convertView.findViewById(R.id.navigationIconImageView);
        navigationIcon.setImageResource(navigationTarget.getNavigationIcon());

        return convertView;
    }
}
