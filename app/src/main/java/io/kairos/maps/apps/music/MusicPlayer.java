package io.kairos.maps.apps.music;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import io.kairos.maps.KairosApplication;
import io.kairos.maps.ui.Notifier;

/**
 * Controls the playback of songs and provides information about the current song being played.
 */
public class MusicPlayer implements MediaPlayer.OnCompletionListener {
    private static MusicPlayer instance;

    private MusicPlayer() {
        this.context = KairosApplication.getAppContext();
        this.songs = getSongList();
    }

    public static MusicPlayer instance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }

        return instance;
    }

    private Song currentSong;
    private Context context;
    private MediaPlayer mediaPlayer;
    private List<Song> songs;

    private SongCompletedListener songCompletedListener;

    public void requestSongCompletion(SongCompletedListener songCompletedListener) {
        this.songCompletedListener = songCompletedListener;
    }

    public void play(Song song) {
        Uri contentUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.getID());

        // STOP already playing song.
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(context, contentUri);
        if (mediaPlayer == null) {
            Notifier.notify(context, "Song unavailable");
            return;
        }
        mediaPlayer.setOnCompletionListener(this);

        mediaPlayer.start();
        this.currentSong = song;
    }

    private List<Song> getSongList() {
        List<Song> songsList = new ArrayList<Song>();

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songsList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }

        Collections.sort(songsList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        return songsList;
    }

    public List<Song> getSongs() {
        return songs;
    }


    public Song getCurrentSong() {
        return currentSong;
    }

    public boolean isPlaying() {
        if (mediaPlayer == null) return false;

        return mediaPlayer.isPlaying();
    }

    public void playOrPause() {
        if (isPlaying()) {
            mediaPlayer.pause();
            return;
        }

        if (currentSong != null) {
            mediaPlayer.start();
            return;
        }

        shuffle();
    }

    public void shuffle() {
        currentSong = songs.get(new Random().nextInt(songs.size()));
        play(currentSong);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (this.songCompletedListener != null) {
            this.songCompletedListener.onSongCompleted();
        }
    }
}
