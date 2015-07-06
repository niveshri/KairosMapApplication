package io.kairos.maps.apps.music;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.kairos.maps.R;
import io.kairos.maps.apps.KairosApp;
import io.kairos.maps.ui.Notification;
import io.kairos.maps.utils.Utils;

public class MusicApp implements KairosApp {
    @Override
    public String getName() {
        return "Music";
    }

    @Override
    public Fragment getAppMainFragment() {
        return new MusicFragment();
    }

    @Override
    public View getAppDeckView(Context context, ViewGroup parent) {
        View appDeckView = LayoutInflater.from(context).inflate(
                R.layout.music_app_tile, parent, false);

        Song song = MusicPlayer.instance().getCurrentSong();

        TextView musicPrimaryInfo = (TextView) appDeckView.findViewById(R.id.musicPrimaryInfo);
        TextView musicSecondaryInfo = (TextView) appDeckView.findViewById(R.id.musicSecondaryInfo);

        musicPrimaryInfo.setText(song != null ? Utils.trimString(song.getTitle(), 9) : "Off");
        musicSecondaryInfo.setText(song != null ? Utils.trimString(song.getArtist(), 9) : "");

        return appDeckView;
    }

    @Override
    public View getNotificationView(Context context, ViewGroup parent, Notification notification) {
        return null;
    }
}
