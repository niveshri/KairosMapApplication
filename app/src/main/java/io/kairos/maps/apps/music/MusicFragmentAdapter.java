package io.kairos.maps.apps.music;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import io.kairos.maps.context.CarContextProvider;
import io.kairos.maps.context.DriverLoad;

public class MusicFragmentAdapter extends FragmentStatePagerAdapter {
    private MusicFragment musicFragment;
    private boolean isHighFidelity;

    public MusicFragmentAdapter(FragmentManager fm, MusicFragment musicFragment) {
        super(fm);
        this.musicFragment = musicFragment;
        this.isHighFidelity = CarContextProvider.instance().lastDriverLoad() == DriverLoad.LOW;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new SongsControlFragment();
            case 1:
                return new SongsListFragment();
            default:
                return new SongsControlFragment();
        }
    }

    @Override
    public int getCount() {
        return isHighFidelity ? 2 : 1;
    }
}
