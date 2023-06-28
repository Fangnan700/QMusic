package icu.yvling.qmusic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import icu.yvling.qmusic.R;
import icu.yvling.qmusic.models.Music;

public class MusicAdapter extends ArrayAdapter<Music> {

    private int layoutId;

    public MusicAdapter(Context context, int layoutId, List<Music> list) {
        super(context, layoutId, list);
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int musicNameMaxLength = 30;
        int artistNameMaxLength = 30;
        Music music = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
        TextView musicNameView = (TextView) view.findViewById(R.id.music_name);
        TextView artistNameView = (TextView) view.findViewById(R.id.artist_name);
        TextView musicDurationView = (TextView) view.findViewById(R.id.musicDuration);

        if(music.getMusicName().length() > musicNameMaxLength) {
            musicNameView.setText(music.getMusicName().substring(0, musicNameMaxLength) + "...");
        } else {
            musicNameView.setText(music.getMusicName());
        }
        if(music.getArtistName().length() > artistNameMaxLength) {
            artistNameView.setText(music.getArtistName().substring(0, artistNameMaxLength) + "...");
        } else {
            artistNameView.setText(music.getArtistName());
        }
        musicDurationView.setText(formatDuration(music.getDuration()));

        return view;
    }

    /*
     * 时间格式化
     * */
    public static String formatDuration(long t) {
        long seconds = t / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}
