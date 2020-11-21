package com.jerryjspringer.musicapp.audio.model;

public class ArtistModel {
    private String mArtist;
    private int mTracks;
    private int mAlbums;

    public ArtistModel(String artist, int tracks, int albums) {
        mArtist = artist;
        mTracks = tracks;
        mAlbums = albums;
    }

    public String getArtist() {
        return mArtist;
    }

    public int getTracks() {
        return mTracks;
    }

    public int getAlbums() {
        return mAlbums;
    }
}
