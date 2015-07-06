package io.kairos.maps.apps.music;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.kairos.maps.R;
import io.kairos.maps.ui.BackButtonHandler;

public class MusicFragment extends Fragment implements BackButtonHandler {
    private ViewPager viewPager;
    private MusicFragmentAdapter musicFragmentAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        musicFragmentAdapter = new MusicFragmentAdapter(getChildFragmentManager(), this);

        viewPager = (ViewPager) getActivity().findViewById(R.id.musicPager);
        viewPager.setAdapter(musicFragmentAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        return view;
    }

    @Override
    public boolean onBackButtonPressed() {
        try {
            BackButtonHandler backButtonHandler =
                    (BackButtonHandler) getActivity().getSupportFragmentManager().findFragmentByTag(
                            "android:switcher:" + R.id.musicPager + ":" + viewPager.getCurrentItem());

            if (backButtonHandler == null) return false;

            return backButtonHandler.onBackButtonPressed();
        } catch (ClassCastException ce) {
            return false;
        }
    }
}
