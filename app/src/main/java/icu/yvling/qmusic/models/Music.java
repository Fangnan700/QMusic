package icu.yvling.qmusic.models;

import androidx.annotation.NonNull;

public class Music {
    private int index;
    private String musicName;
    private String artistName;
    private String sourcePath;
    private long duration;


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @Override
    public String toString() {
        return "Music{" +
                "musicName='" + musicName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", sourcePath='" + sourcePath + '\'' +
                ", duration=" + duration +
                '}';
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Music(int index, String musicName, String artistName, String sourcePath) {
        this.index = index;
        this.musicName = musicName;
        this.artistName = artistName;
        this.sourcePath = sourcePath;
    }

    public Music() {
    }

}
