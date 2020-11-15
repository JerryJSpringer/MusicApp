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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.jerryjspringer.musicapp.audio.AudioService;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

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

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            return;
        }


        Intent intent = new Intent(this, AudioService.class);
        startService(intent);
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
}