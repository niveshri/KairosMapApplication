package io.kairos.maps.apps.music;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import io.kairos.maps.R;
import io.kairos.maps.utils.Utils;

public class SongsControlFragment extends Fragment implements SongCompletedListener {
    private TextView songNameTextView;
    private ImageButton playPauseButton;
    private TextView songArtistTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MusicPlayer.instance().requestSongCompletion(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs_control, container, false);

        songNameTextView = (TextView) view.findViewById(R.id.songName);
        songArtistTextView = (TextView) view.findViewById(R.id.songArtist);
        playPauseButton = (ImageButton) view.findViewById(R.id.playPauseButton);

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.instance().playOrPause();
                updateUIBasedOnState();
            }
        });

        ImageButton shuffleButton = (ImageButton) view.findViewById(R.id.shuffleButton);
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.instance().shuffle();
                updateUIBasedOnState();
            }
        });

        updateUIBasedOnState();

        return view;
    }

    private void updateUIBasedOnState() {
        Song currentSong = MusicPlayer.instance().getCurrentSong();
        songNameTextView.setText(currentSong != null ? Utils.trimString(currentSong.getTitle(), 12) : "Off");
        songArtistTextView.setText(currentSong != null ? Utils.trimString(currentSong.getArtist(), 12) : "");

        boolean isPlaying = MusicPlayer.instance().isPlaying();
        playPauseButton.setImageResource(isPlaying ? R.drawable.pause : R.drawable.play);
    }

    @Override
    public void onSongCompleted() {
        updateUIBasedOnState();
    }
}
