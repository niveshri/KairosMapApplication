package io.kairos.maps.apps.music;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.kairos.maps.R;
import io.kairos.maps.speechrec.MicButton;
import io.kairos.maps.speechrec.SpeechToTextListener;
import io.kairos.maps.ui.BackButtonHandler;
import io.kairos.maps.ui.Notifier;

public class SongsListFragment extends ListFragment implements
        AdapterView.OnItemClickListener, SpeechToTextListener, BackButtonHandler {
    private static final String TAG = SongsListFragment.class.toString();

    private SongsAdapter songsAdapter;

    private boolean filtered;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        songsAdapter = new SongsAdapter(getActivity(), MusicPlayer.instance().getSongs());
        setListAdapter(songsAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs_list, container, false);

        MicButton micButton = (MicButton) view.findViewById(R.id.micButton);
        micButton.setSpeechToTextListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        getListView().setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Song song = songsAdapter.getItem(position);

        MusicPlayer.instance().play(song);
    }

    @Override
    public void onSpeechToText(List<String> textList) {
        if (textList == null || textList.size() == 0) return;

        songsAdapter.getFilter().filter(textList.get(0));
        filtered = true;
    }

    @Override
    public boolean onBackButtonPressed() {
        if (!filtered) {
            return false;
        }

        filtered = false;
        songsAdapter = new SongsAdapter(getActivity(), MusicPlayer.instance().getSongs());
        setListAdapter(songsAdapter);

        return true;
    }
}