package com.jerryjspringer.musicapp.audio;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.media.MediaSessionManager;

import com.jerryjspringer.musicapp.MainActivity;
import com.jerryjspringer.musicapp.audio.model.AudioModel;
import com.jerryjspringer.musicapp.audio.util.StorageUtil;

import java.io.IOException;
import java.util.List;

public class AudioService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {

    // Actions
    public static final String ACTION_PLAY = "com.jerryjspringer.musicapp.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.jerryjspringer.musicapp.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.jerryjspringer.musicapp.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.jerryjspringer.musicapp.ACTION_NEXT";
    public static final String ACTION_STOP = "com.jerryjspringer.musicapp.ACTION_STOP";

    // MediaSession
    private MediaSessionManager mMediaSessionManager;
    private MediaSession mMediaSession;
    private MediaController.TransportControls mTransportControls;

    // AudioPlayer Notification ID
    private static final int NOTIFICATION_ID = 101;

    // Binder that will be given to clients
    private final IBinder mIBinder = new LocalBinder();

    // MediaPlayer
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;

    // Audio Files
    private List<AudioModel> mPlaylist;
    private AudioModel mCurrentAudio;
    private int mAudioIndex;
    private int resumePosition;

    private final BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get the new media index from SharedPreferences
            StorageUtil storageUtil = new StorageUtil(getApplicationContext());
            mPlaylist = storageUtil.loadCurrentPlaylist();
            mAudioIndex = storageUtil.loadAudioIndex();

            // Check indices
            if (mAudioIndex != -1 && mAudioIndex < mPlaylist.size())
                mCurrentAudio = mPlaylist.get(mAudioIndex);
            else
                stopSelf();

            // Reset and play new audio
            stopMedia();
            mMediaPlayer.reset();
            initMediaPlayer();

            // TODO Notifications, playback status, and metadata
            /*
            updateMetaData();
            buildNotification(PlaybackStatus.PLAYING);
             */
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            StorageUtil util = new StorageUtil(getApplicationContext());
            mPlaylist = util.loadCurrentPlaylist();
            mAudioIndex = util.loadAudioIndex();

            if (-1 < mAudioIndex && mAudioIndex < mPlaylist.size()) {
                mCurrentAudio = mPlaylist.get(mAudioIndex);
            } else {
                stopSelf();
            }

        } catch (NullPointerException e) {
            stopSelf();
        }

        if (!requestAudioFocus())
            stopSelf();

        if (mMediaSessionManager == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO Add other broadcast receivers (become noisy, call state, etc)
        register_playNewAudio();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            stopMedia();
            mMediaPlayer.release();
        }

        removeAudioFocus();
        unregisterReceiver(playNewAudio);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        // This will be called when the audio focus of the system changes
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // Resuming playback
                if (mMediaPlayer == null)
                    initMediaPlayer();
                else if (!mMediaPlayer.isPlaying())
                    mMediaPlayer.start();

                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for some amount of time stop and release
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus temporarily so playback is paused
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus but playing at lowered volume is okay
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    private boolean requestAudioFocus() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            return true;
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                mAudioManager.abandonAudioFocus(this);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMedia();
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("Audio Service",
                        "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;

            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("Audio Service",
                        "MEDIA ERROR SERVER DIED" + extra);
                break;

            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("Audio Service",
                        "MEDIA ERROR UNKNOWN " + extra);
                break;
        }

        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
    }

    private void playMedia() {
        if (!mMediaPlayer.isPlaying())
            mMediaPlayer.start();
    }

    private void stopMedia() {
        if (mMediaPlayer == null)
            mMediaPlayer.stop();
    }

    private void pauseMedia() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            resumePosition = mMediaPlayer.getCurrentPosition();
        }
    }

    private void resumeMedia() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(resumePosition);
            mMediaPlayer.start();
        }
    }

    private void skipToNext() {
        if (mAudioIndex == mPlaylist.size() - 1) {
            mAudioIndex = 0;
            mCurrentAudio = mPlaylist.get(mAudioIndex);
        } else {
            mCurrentAudio = mPlaylist.get(++mAudioIndex);
        }

        new StorageUtil(getApplicationContext()).storeAudioIndex(mAudioIndex);

        stopMedia();
        mMediaPlayer.reset();
        initMediaPlayer();
    }

    private void skipToPrevious() {
        if (mAudioIndex == 0) {
            mAudioIndex = mPlaylist.size() - 1;
            mCurrentAudio = mPlaylist.get(mAudioIndex);
        } else {
            mCurrentAudio = mPlaylist.get(--mAudioIndex);
        }

        new StorageUtil(getApplicationContext()).storeAudioIndex(mAudioIndex);

        stopMedia();
        mMediaPlayer.reset();
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        // Create a new media player for music
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        // Setup event listeners
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnInfoListener(this);

        // Resetting the media player to make sure it is not pointing to another source
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mMediaPlayer.setDataSource(mCurrentAudio.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }

        mMediaPlayer.prepareAsync();
    }

    @SuppressLint("ServiceCast")
    private void initMediaSession() throws RemoteException {
        if (mMediaSessionManager != null)
            return;

        mMediaSessionManager = MediaSessionManager.getSessionManager(getApplicationContext());

        // Create a new MediaSession
        mMediaSession = new MediaSession(getApplicationContext(), "MusicApp");

        // Get MediaSessions transport controls
        mTransportControls = mMediaSession.getController().getTransportControls();

        // Set MediaSession ready to receive media commands
        mMediaSession.setActive(true);

        // Indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback
        mMediaSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Attach Callback to receive MediaSession updates
        mMediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
                // buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
            }

            @Override
            public void onStop() {
                super.onStop();
                stopSelf();
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
            }
        });
    }

    /*
    private void buildNotification(PlaybackStatus playbackStatus) {

        int notificationAction = R.drawable.ic_media_pause;
        PendingIntent play_pauseAction = null;

        // Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = R.drawable.ic_media_pause;

            // Create the pause action
            play_pauseAction = playbackAction(1);

        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = R.drawable.ic_media_play;

            // Create the play action
            play_pauseAction = playbackAction(0);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.image);

        // Create a new Notification
        Notification.Builder notificationBuilder = (Notification.Builder)
                new Notification.Builder(this)
                .setShowWhen(false)
                // Set style
                .setStyle(new Notification.MediaStyle()
                        // Attach MediaSession token
                        .setMediaSession(MediaSession)
                        // Show playback controls in the compact notification view
                        .setShowActionsInCompactView(0, 1, 2))
                // Set Notification color
                        .setColor(getResources().getColor(R.color.colorPrimary))
                //

    }
     */

    private void register_playNewAudio() {
        // Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(MainActivity.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }

    public class LocalBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }
}
