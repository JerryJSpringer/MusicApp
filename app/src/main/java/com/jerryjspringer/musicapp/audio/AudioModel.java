package com.jerryjspringer.musicapp.audio;

public class AudioModel {
    private String mPath;
    private String mArtist;
    private String mAlbum;
    private String mYear;
    private String mTrack;
    private String mTitle;
    private String mDuration;

    public AudioModel (String path, String artist, String album, String year,
                       String track, String title, String duration) {
        mPath = path;
        mArtist = artist;
        mAlbum = album;
        mYear = year;
        mTrack = track;
        mTitle = title;
        mDuration = duration;
    }

    public String getPath() {
        return mPath;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public String getYear() {
        return mYear;
    }

    public String getTrack() {
        return mTrack;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDuration() {
        return mDuration;
    }
}
