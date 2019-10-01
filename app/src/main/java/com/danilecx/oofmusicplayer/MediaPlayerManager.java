package com.danilecx.oofmusicplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Random;

class MediaPlayerManager {

    private boolean mediaPlayerPrepared, shuffleSong, loopingSong;
    private int lastSongID, lastSelectedMusicLV;

    private WeakReference<MainActivity> activityReference;

    private MediaPlayer mediaPlayer;
    private TextView songNameTV, totDurTV, curPosTV;
    private SeekBar seekBar;
    private ListView musicListView;
    private ImageButton playButton;

    private Handler seekbarUpdateHandler;
    private Runnable updateSeekbar;


    MediaPlayerManager(MainActivity context) {
        activityReference = new WeakReference<>(context);
        MainActivity activity = activityReference.get();
        if (activity == null || activity.isFinishing()) return;

        songNameTV = activity.findViewById(R.id.songTextView);
        totDurTV = activity.findViewById(R.id.totalDurationTextView);
        curPosTV = activity.findViewById(R.id.curPosTextView);
        seekBar = activity.findViewById(R.id.seekBar);
        musicListView = activity.findViewById(R.id.musicListView);
        playButton = activity.findViewById(R.id.playButton);
        songNameTV.setSelected(true);
    }

    void setLastSelectedMusicLV(int lastSelectedMusicLV) {
        this.lastSelectedMusicLV = lastSelectedMusicLV;
    }

    boolean isLooping() {
        return loopingSong;
    }

    void setLooping(boolean loop) {
        loopingSong = loop;
        mediaPlayer.setLooping(loop);
    }

    boolean isShuffling() {
        return shuffleSong;
    }

    void setShuffle(boolean shuffleSong) {
        this.shuffleSong = shuffleSong;
    }

    // Set MediaPlayer to play music onPrepared()
    void initialize() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayerPrepared = false;
        lastSongID = 0;

        // Set Handler for seekBar progress when mediaPlayer plays
        seekbarUpdateHandler = new Handler();
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                int curPosMilli = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(curPosMilli);
                curPosTV.setText(String.format(Locale.getDefault(), "%2d:%02d", (curPosMilli / 1000 / 60) % 60, (curPosMilli / 1000) % 60));
                seekbarUpdateHandler.postDelayed(this, 50);
            }
        };

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayerPrepared = true;
                int durationMilli = mediaPlayer.getDuration();
                totDurTV.setText(String.format(Locale.getDefault(), "%2d:%02d", (durationMilli / 1000 / 60) % 60, (durationMilli / 1000) % 60));
                seekBar.setMax(durationMilli);
                seekbarUpdateHandler.postDelayed(updateSeekbar, 0);
                mediaPlayer.start();
                String songName = (String) musicListView.getItemAtPosition(lastSelectedMusicLV);
                songNameTV.setText(songName);
                playButton.setImageResource(R.drawable.pause_button);
                Log.e("INFO", "Playing music!");
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!loopingSong)
                    playNextSong();
            }
        });
    }

    void playSongAtIndex(Integer position) {
        MainActivity activity = activityReference.get();
        if (activity == null || activity.isFinishing()) return;

        // Play music of item clicked
        String internalPath = activity.getApplicationContext().getFilesDir().getAbsolutePath();
        String songTitle = musicListView.getItemAtPosition(position).toString();
        File musicFile = new File(internalPath + "/" + musicListView.getItemAtPosition(position).toString() + ".mp3");

        // Reset seekbar and song title
        songNameTV.setText("");
        totDurTV.setText(activity.getString(R.string.default_seek));
        curPosTV.setText(activity.getString(R.string.default_seek));
        seekbarUpdateHandler.removeCallbacks(updateSeekbar);
        seekBar.setProgress(0);

        //Reset mediaplayer
        mediaPlayer.reset();
        mediaPlayerPrepared = false;
        mediaPlayer.setLooping(loopingSong);
        lastSongID = position;

        if (!musicFile.exists())
            new DownloadFile(activity).execute(internalPath, songTitle);
        else
            this.prepare(musicFile.getPath());
    }

    void seekTo(Integer progress) {
        if (mediaPlayer != null && mediaPlayerPrepared) {
            mediaPlayer.seekTo(progress);
            if (mediaPlayer.isPlaying())
                mediaPlayer.start();
        }
    }

    void switchPlayPause(View view) {
        if (mediaPlayer != null && mediaPlayerPrepared) {
            if (mediaPlayer.isPlaying()) {
                ((ImageButton) view).setImageResource(R.drawable.play_button);
                mediaPlayer.pause();
            } else {
                ((ImageButton) view).setImageResource(R.drawable.pause_button);
                mediaPlayer.start();
            }
        }
    }

    void playNextSong() {
        int totalItems = musicListView.getAdapter().getCount();

        if (shuffleSong)
            lastSongID = new Random().nextInt(totalItems);
        else
            lastSongID++;

        if (lastSongID >= totalItems) {
            lastSongID = 0;
        }
        lastSelectedMusicLV = lastSongID;
        playSongAtIndex(lastSongID);


    }

    void playPreviousSong() {
        int totalItems = musicListView.getAdapter().getCount();

        if (shuffleSong)
            lastSongID = new Random().nextInt(totalItems);
        else
            lastSongID--;

        if (lastSongID < 0) {
            lastSongID = totalItems - 1;
        }
        lastSelectedMusicLV = lastSongID;
        playSongAtIndex(lastSongID);
    }

    void prepare(String path) {
        try {
            mediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
    }
}