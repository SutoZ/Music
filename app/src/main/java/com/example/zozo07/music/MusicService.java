package com.example.zozo07.music;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Zozo07 on 2016.07.15..
 */
//Extend the opening line of the class declaration to implement some interfaces we will use for music playback:
//MUSIC IS PLAYED FROM HERE
public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    //media player
    private MediaPlayer mediaPlayer;
    //song list
    private ArrayList<Song> songList;
    //current position of the track
    private int songPos;
    //represtens the inner Binding class we created
    private final IBinder musicBind = new MusicBinder();


    @Override
    public void onCreate() {
        super.onCreate();
        songPos = 0;
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    public void playSong() {
        //play a song
        mediaPlayer.reset();
        //get song
        Song playSong = songList.get(songPos);
        //get id
        long currSong = playSong.getID();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);

        try {
            mediaPlayer.setDataSource(getApplication(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mediaPlayer.prepareAsync();
    }


    public void initMusicPlayer() {
        //we configure the music player
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs) {
        songList = theSongs;
    }

    //We will call this when the user picks a song from the list.
    public void setSong(int songindex) {
        songPos = songindex;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mediaPlayer.start();
    }
}
