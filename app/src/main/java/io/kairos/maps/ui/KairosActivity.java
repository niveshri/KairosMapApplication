package io.kairos.maps.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.lucasr.twowayview.TwoWayView;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.kairos.maps.R;
import io.kairos.maps.apps.KairosApp;
import io.kairos.maps.apps.KairosAppLoader;
import io.kairos.maps.apps.navigation.NavigationAppFragment;
import io.kairos.maps.apps.navigation.NavigationTarget;
import io.kairos.maps.context.CarContextListener;
import io.kairos.maps.context.CarContextProvider;
import io.kairos.maps.context.CarState;
import io.kairos.maps.context.DriverLoad;
import io.kairos.maps.context.DriverLoadListener;
import io.kairos.maps.providers.TouchpadInputListener;
import io.kairos.maps.providers.TouchpadInputProvider;
import io.kairos.maps.providers.TouchpadInputType;


public class KairosActivity extends FragmentActivity
        implements TouchpadInputListener, NavigationAppFragment.NavigationTargetSelectedListener,
        DriverLoadListener, CarContextListener,
        NotificationFragment.NotificationsActivatedListener {
    private static final String TAG = KairosActivity.class.toString();

    private List<KairosApp> kairosApps;

    private ArrayAdapter<KairosApp> appDeckAdapter;
    private TwoWayView appDeckGridView;
    private BackButtonHandler currentAppFragment;

    private FrameLayout appInfoFrameLayout;
    private NavigationFragment navigationFragment;
    private NotificationFragment notificationFragment;

    private ImageView contextImageView;
    private TextView contextTextView;

    private State state;

    private static enum State {
        PLAIN,
        APP_DECK_ACTIVE,
        APP_MAIN_ACTIVE
    }

    private List<String> localDeals = Arrays.asList(
            "Starbucks - 5$ off on Gift Card",
            "Giant Eagle - 10% off on Membership",
            "Shell Gas Station 2 mins away",
            "Sam's Club - 10% off membership",
            "Costco - 15% off goods"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_kairos);

        kairosApps = KairosAppLoader.instance().getKairosApps();

        appDeckAdapter = new AppAdapter(this, kairosApps);

        appDeckGridView = (TwoWayView) findViewById(R.id.appDeckGridView);
        appDeckGridView.setAdapter(appDeckAdapter);
        appDeckGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayAppInfoFragment((int) id);
            }
        });

        contextImageView = (ImageView) findViewById(R.id.contextImageView);
        contextTextView = (TextView) findViewById(R.id.contextTextView);

        appInfoFrameLayout = (FrameLayout) findViewById(R.id.appInfoFrameLayout);

        navigationFragment = new NavigationFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.navigationFrameLayout, navigationFragment).commit();

        notificationFragment = new NotificationFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.notificationFrameLayout, notificationFragment).commit();

        TouchpadInputProvider.instance().requestInputControls(this);
        CarContextProvider.instance().requestDriverLoad(this);
        CarContextProvider.instance().requestCarStates(this);
    }

    private void displayAppInfoFragment(int appIndex) {
        Fragment appFragment = kairosApps.get(appIndex).getAppMainFragment();

        displayFragmentInMainContainer(appFragment);
    }

    private void displayFragmentInMainContainer(Fragment fragment) {
        setUIState(State.APP_MAIN_ACTIVE);

        if (appInfoFrameLayout.getChildCount() != 0) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.appInfoFrameLayout, fragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.appInfoFrameLayout, fragment).commit();
        }

        try {
            currentAppFragment = (BackButtonHandler) fragment;
        } catch (ClassCastException e) { }
    }

    private void setUIState(State state) {
        this.state = state;

        switch (state) {
            case PLAIN:
                appDeckGridView.setVisibility(View.GONE);
                appInfoFrameLayout.setVisibility(View.GONE);
                break;
            case APP_DECK_ACTIVE:
                appDeckAdapter.notifyDataSetChanged();
                appDeckGridView.setVisibility(View.VISIBLE);
                appInfoFrameLayout.setVisibility(View.GONE);
                break;
            case APP_MAIN_ACTIVE:
                appDeckGridView.setVisibility(View.GONE);
                appInfoFrameLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        setUIState(State.APP_DECK_ACTIVE);
    }

    @Override
    public void onBackPressed() {
        // If current app is present on back stack, allow it to handle the back press.
        // If it doesn't do anything, handle it yourself.
        if (currentAppFragment != null) {
            if (currentAppFragment.onBackButtonPressed()) return;
        }

        switch (state) {
            case PLAIN:
                super.onBackPressed();
                break;
            case APP_DECK_ACTIVE:
//                setUIState(State.PLAIN);
                break;
            case APP_MAIN_ACTIVE:
                setUIState(State.APP_DECK_ACTIVE);
                currentAppFragment = null;
                break;
        }
    }

    @Override
    public void onNavigationTargetSelected(NavigationTarget navigationTarget) {
        navigationFragment.route(navigationTarget.getAddress());
        this.onBackPressed();
    }

    @Override
    public void onMockRouteSelected() {
        navigationFragment.mockRoute();
        this.onBackPressed();
    }

    @Override
    public void onInputReceived(TouchpadInputType inputType) {
        switch (inputType) {
            case SWIPE_LEFT:
                break;
            case SWIPE_RIGHT:
                break;
            case SWIPE_UP:
                break;
            case SWIPE_DOWN:
                break;
            case CLICK_SELECT:
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
                break;
            case CLICK_MENU:
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                });
                break;
            case CLICK_A:
                break;
            case CLICK_B:
                break;
        }
    }

    @Override
    public void onCarStateReceived(final CarState carState) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                contextTextView.setText(carState.toString());
            }
        });
    }

    private void applyToUIThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    @Override
    public void onCarStateChanged(CarState carState) {
        final TextView trivia = (TextView) findViewById(R.id.triviaBoxFrameLayout);
        //if (carState != CarState.STOPPED_SIGNAL && carState != CarState.STOPPED_OUT_OF_TRAFFIC) {
        if (carState != CarState.STOPPED) {
            applyToUIThread(new Runnable() {
                @Override
                public void run() {
                    trivia.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onCarStateChangedWithThreshold(CarState carState) {
        final TextView trivia = (TextView) findViewById(R.id.triviaBoxFrameLayout);
        //if (carState == CarState.STOPPED_SIGNAL || carState == CarState.STOPPED_OUT_OF_TRAFFIC) {
        if (carState == CarState.STOPPED) {
            applyToUIThread(new Runnable() {
                @Override
                public void run() {
                    // Show trivia fragment here.
                    trivia.setVisibility(View.VISIBLE);
                    trivia.setText(chooseRandomDeal());
                }
            });
        }
    }

    private String chooseRandomDeal() {
        return localDeals.get(new Random().nextInt(localDeals.size()));
    }

    @Override
    public void onDriverLoadReceived(final DriverLoad driverLoad) {
        applyToUIThread(new Runnable() {
            @Override
            public void run() {
                contextImageView.setBackgroundColor(
                        driverLoad == DriverLoad.LOW ? Color.GREEN : Color.RED);
            }
        });
    }

    @Override
    public void onDriverLoadChanged(DriverLoad driverLoad) {
    }

    @Override
    public void onDriverLoadChangedWithThreshold(DriverLoad driverLoad) {
    }

    @Override
    public void onNotificationsActivated() {
        displayFragmentInMainContainer(new NotificationsListFragment());
    }
}
