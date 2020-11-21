package com.jerryjspringer.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.jerryjspringer.musicapp.audio.model.AudioModel;
import com.jerryjspringer.musicapp.audio.AudioService;
import com.jerryjspringer.musicapp.audio.AudioUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String Broadcast_PLAY_NEW_AUDIO = "com.jerryjspringer.musicapp.PlayNewAudio";

    // UI Items
    private AppBarConfiguration mAppBarConfiguration;

    // Audio Services
    private AudioService mAudioService;
    private boolean mServiceBound;

    // Audio Files
    private List<AudioModel> mAudioList;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.LocalBinder binder = (AudioService.LocalBinder) service;
            mAudioService = binder.getService();
            mServiceBound = true;

            Toast.makeText(MainActivity.this,
                    "Audio Service Bound",
                    Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment =  (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container_main);
        NavController navController = navHostFragment.getNavController();
        NavigationView navView = findViewById(R.id.nav_main_drawer_view);

        // Setup the layout for drawer
        DrawerLayout layout = findViewById(R.id.drawer_layout_root_main);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_library,
                R.id.playlistFragment)
                .setOpenableLayout(layout)
                .build();

        // Setup navigation menus
        NavigationUI.setupWithNavController(navView, navController);

        // Setup toolbar
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        // Checks permissions and if not they are requested
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(
                this, R.id.fragment_container_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    // TODO ? onSaveInstanceState and onRestoreInstanceState see 7

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mAudioService.stopSelf();
        }
    }

    public List<AudioModel> loadAudio() {
        mAudioList = AudioUtil.getAllAudio(this);
        return mAudioList;
    }

    public void playAudio(int audioIndex) {
        // Checks if the service is active
        AudioUtil util = new AudioUtil(getApplicationContext());

        if (!mServiceBound) {
            // Store serializable audio list to SharedPreferences
            util.storeAudio(mAudioList);
            util.storeAudioIndex(audioIndex);

            Intent audioIntent = new Intent(this, AudioService.class);
            startService(audioIntent);
            bindService(audioIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            // Store the new audioIndex to SharedPreferences
            util.storeAudioIndex(audioIndex);

            // Service is active
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }
}