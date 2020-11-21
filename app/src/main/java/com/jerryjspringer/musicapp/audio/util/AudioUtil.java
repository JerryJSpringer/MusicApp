package com.jerryjspringer.musicapp.audio.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.jerryjspringer.musicapp.audio.model.ArtistModel;
import com.jerryjspringer.musicapp.audio.model.AudioModel;

import java.util.ArrayList;
import java.util.List;

public final class AudioUtil {

    private AudioUtil() {
    }

    public static List<ArtistModel> getArtists(final Context context) {
        final List<ArtistModel> artistList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS};

        String sortOrder = MediaStore.Audio.Artists.ARTIST + " ASC";

        Cursor c = context.getContentResolver()
                .query(uri, projection, null, null, sortOrder);

        if (c != null) {
            Log.d("AUDIO PLAYER", "count: " + c.getCount());

            while (c.moveToNext()) {
                // Create a model object
                ArtistModel artistModel;

                // Get the data
                String artist = c.getString(0);
                int tracks = c.getInt(1);
                int albums = c.getInt(2);

                Log.d("AUDIO PLAYER", "Artist: " + artist);

                // Set data to the model object
                artistModel = new ArtistModel(artist, tracks, albums);

                // Add the model object to the list
                artistList.add(artistModel);
            }

            c.close();
        }

        // Return the list
        return artistList;
    }

    public static List<AudioModel> getAlbumSongs(final Context context, final String album) {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media.ALBUM + " = '" + album + "'";
        String sortOrder = MediaStore.Audio.Media.TRACK + " ASC";
        return getSongs(context, selection, sortOrder);
    }

    public static List<AudioModel> getArtistSongs(final Context context, final String artist) {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND "
                + MediaStore.Audio.Media.ARTIST + " = '" + artist + "'";
        String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC, "
                + MediaStore.Audio.Media.TRACK + " ASC, "
                + MediaStore.Audio.Media.TITLE + " ASC";
        return getSongs(context, selection, sortOrder);
    }

    public static List<AudioModel> getAllAudio(final Context context) {
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        return getSongs(context, selection, sortOrder);
    }

    private static List<AudioModel> getSongs(final Context context,
                                             String selection, String sortOrder) {
        final List<AudioModel> audioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.YEAR,
                MediaStore.Audio.AudioColumns.TRACK,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DURATION};

        Cursor c = context.getContentResolver()
                .query(uri, projection, selection, null, sortOrder);

        if (c != null) {
            Log.d("AUDIO PLAYER", "count: " + c.getCount());
            while (c.moveToNext()) {
                // Create a model object
                AudioModel audioModel;

                // Get the data
                String path = c.getString(0);
                String artist = c.getString(1);
                String album = c.getString(2);
                String year = c.getString(3);
                String track = c.getString(4);
                String title = c.getString(5);
                String duration = c.getString(6);

                Log.d("AUDIO PLAYER", "name: " + title);

                // Set data to the model object
                audioModel = new AudioModel(path, artist, album, year, track, title, duration);

                // Add the model object to the list
                audioList.add(audioModel);
            }

            c.close();
        }

        // Return the list
        return audioList;
    }
}
