package com.jerryjspringer.musicapp.audio;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioService extends Service {

    private MediaPlayer mMediaPlayer;
    private List<AudioModel> mAudio;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Rescans for audio files on start
        mAudio = getAllAudioFromDevice(getApplicationContext());

        try {
            mMediaPlayer.setDataSource(mAudio.get(7).getPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            Log.d("AUDIO PLAYER", "PLAYING: ");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a new media player for music
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private List<AudioModel> getAllAudioFromDevice(final Context context) {
        final List<AudioModel> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.YEAR,
                MediaStore.Audio.AudioColumns.TRACK,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DURATION};

        /*
        Cursor c = context.getContentResolver()
                .query(uri,
                        projection,
                        MediaStore.Audio.Media._ID + " like ? ",
                        new String[]{"%utm%"},
                        null);
         */

        Cursor c = getContentResolver()
                .query(uri, projection, null, null, null);

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
                tempAudioList.add(audioModel);
            }

            c.close();
        }

        // Return the list
        return tempAudioList;
    }
}
