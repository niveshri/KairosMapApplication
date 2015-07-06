package io.kairos.maps.apps.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import io.kairos.maps.R;

public class SongsAdapter extends ArrayAdapter<Song> {
    public SongsAdapter(Context context, List<Song> songList){
        super(context, android.R.layout.simple_list_item_2, songList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Song song = getItem(position);

        convertView = LayoutInflater.from(getContext())
                .inflate(R.layout.song_list_item, viewGroup, false);

        TextView songView = (TextView)convertView.findViewById(R.id.song_title);
        TextView artistView = (TextView)convertView.findViewById(R.id.song_artist);

        songView.setText(song.getTitle());
        artistView.setText(song.getArtist());

        convertView.setTag(position);
        return convertView;
    }
}
