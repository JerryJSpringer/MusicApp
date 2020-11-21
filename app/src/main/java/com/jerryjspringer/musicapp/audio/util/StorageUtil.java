package com.jerryjspringer.musicapp.audio.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jerryjspringer.musicapp.audio.model.AudioModel;

import java.lang.reflect.Type;
import java.util.List;

public class StorageUtil {
    private final String CURRENT_AUDIO = "CURRENT_AUDIO";
    private final String AUDIO_INDEX = "AUDIO_INDEX";
    private final String STORAGE = "com.jerryjspringer.musicapp.STORAGE";
    private final Context mContext;
    private SharedPreferences mPreferences;


    public StorageUtil(Context context) {
        mContext = context;
    }

    public void storeAudioIndex(int index) {
        mPreferences = mContext.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(AUDIO_INDEX, index);
        editor.apply();
    }

    public int loadAudioIndex() {
        mPreferences = mContext.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        // -1 is the default value returned if nothing is found
        return mPreferences.getInt(AUDIO_INDEX, -1);
    }

    public void storeCurrentPlaylist(List<AudioModel> playList) {
        mPreferences = mContext.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(playList);
        editor.putString(CURRENT_AUDIO, json);
        editor.apply();
    }

    public List<AudioModel> loadCurrentPlaylist() {
        mPreferences = mContext.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPreferences.getString(CURRENT_AUDIO, null);
        Type type = new TypeToken<List<AudioModel>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void clearCache() {
        mPreferences = mContext.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
