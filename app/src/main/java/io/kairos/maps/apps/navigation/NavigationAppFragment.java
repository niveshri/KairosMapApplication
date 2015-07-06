package io.kairos.maps.apps.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import io.kairos.maps.R;
import io.kairos.maps.speechrec.MicButton;
import io.kairos.maps.speechrec.SpeechToTextListener;
import io.kairos.maps.ui.AppState;
import io.kairos.maps.ui.BackButtonHandler;
import io.kairos.maps.utils.Utils;

public class NavigationAppFragment
        extends ListFragment
        implements SpeechToTextListener, AdapterView.OnItemClickListener, BackButtonHandler {
    private final String TAG = NavigationAppFragment.class.toString();

    private NavigationAppAdapter navigationAdapter;
    private boolean filtered = false;

    private RelativeLayout navigationSelectLayout;
    private LinearLayout routingCancelLinearLayout;

    private NavigationTargetSelectedListener navigationTargetSelectedListener;
    private TextView mockRouteTextView;

    public interface NavigationTargetSelectedListener {
        void onNavigationTargetSelected(NavigationTarget navigationTarget);
        void onMockRouteSelected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        navigationAdapter = new NavigationAppAdapter(getActivity());
        setListAdapter(navigationAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_app, container, false);

        MicButton micButton = (MicButton) view.findViewById(R.id.micButton);
        micButton.setSpeechToTextListener(this);

        routingCancelLinearLayout = (LinearLayout) view.findViewById(R.id.routingCancelLinearLayout);
        routingCancelLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppState.instance().remove("routing");
                drawUIBasedOnState();
            }
        });

        mockRouteTextView = (TextView) view.findViewById(R.id.mockRouteButton);
        mockRouteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationTargetSelectedListener.onMockRouteSelected();
            }
        });

        navigationSelectLayout = (RelativeLayout) view.findViewById(R.id.navigationSelectLayout);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        getListView().setOnItemClickListener(this);

        drawUIBasedOnState();
    }

    private void drawUIBasedOnState() {
        if (AppState.instance().get("routing") != null) {
            TextView routingDestinationTextView = (TextView) getActivity().findViewById(R.id.routingDestinationTextView);
            routingDestinationTextView.setText(AppState.instance().get("routing"));

            navigationSelectLayout.setVisibility(View.INVISIBLE);
            routingCancelLinearLayout.setVisibility(View.VISIBLE);
            mockRouteTextView.setVisibility(View.VISIBLE);
        } else {
            navigationSelectLayout.setVisibility(View.VISIBLE);
            routingCancelLinearLayout.setVisibility(View.GONE);
            mockRouteTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            navigationTargetSelectedListener = (NavigationTargetSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NavigationTargetSelectedListener");
        }
    }

    @Override
    public void onSpeechToText(List<String> textList) {
        if (textList == null || textList.size() == 0) return;

        navigationAdapter.filter(textList);
//
//        // Currently filters based on the first recognized speech item. Should do
//        // this using all and also adjusting for slop.
//        navigationAdapter.getFilter().filter(textList.get(0));

        // Draw the first last differently as it represents a fixed address.
        navigationAdapter.insert(new NavigationTarget("To", Utils.capitalize(textList.get(0))),
                navigationAdapter.getCount());
        filtered = true;
    }

    @Override
    public boolean onBackButtonPressed() {
        if (!filtered) {
            return false;
        }

        filtered = false;
        navigationAdapter = new NavigationAppAdapter(getActivity());
        setListAdapter(navigationAdapter);

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NavigationTarget navigationTarget = navigationAdapter.getItem(position);
        navigationTargetSelectedListener.onNavigationTargetSelected(navigationTarget);
    }
}
