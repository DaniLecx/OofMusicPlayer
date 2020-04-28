package com.danilecx.oofmusicplayer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SeekBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    String urlListNames;

    ListView musicListView;
    SeekBar seekBar;
    ImageButton repeatButton, shuffleButton, menuButton;
    MediaPlayerManager mediaPlayerManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicListView = findViewById(R.id.musicListView);
        seekBar = findViewById(R.id.seekBar);
        repeatButton = findViewById(R.id.repeatButton);
        shuffleButton = findViewById(R.id.shuffleButton);
        menuButton = findViewById(R.id.menuButton);

        // Initialize mediaPlayerManager
        mediaPlayerManager = new MediaPlayerManager(this);
        mediaPlayerManager.initialize();

        // Seek music when dragging seekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayerManager.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        // Get a list of song titles from github api
        urlListNames = "https://api.github.com/repos/danilecx/OofMusicPlayer/contents/assets";
        List<String> nameList = new ArrayList<>();
        try {
            nameList = new JsonParser().execute(urlListNames).get();
            Collections.sort(nameList, String.CASE_INSENSITIVE_ORDER);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (nameList.isEmpty()) {
            // Show songs stored in internal files if device is offline
            String internalPath = this.getApplicationContext().getFilesDir().getAbsolutePath();
            File mp3Files = new File(internalPath);
            for (File file : mp3Files.listFiles()) {
                String fileName = file.getName();
                if (fileName.endsWith(".mp3")) {

                    nameList.add(fileName.substring(0, fileName.length() - 4));
                }
            }
        }

        // Bind song titles to ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                nameList);
        musicListView.setAdapter(adapter);

        // Prepare mediaPlayer and play song when song is clicked
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mediaPlayerManager.setLastSelectedMusicLV(position);
                mediaPlayerManager.playSongAtIndex(position);
            }
        });

        // Show privacy policy
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.privacypolicy) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.freeprivacypolicy.com/privacy/view/726b100f5baa1dec206bf50f47f59a3c"));
                            startActivity(browserIntent);
                            return true;
                        }
                        return false;

                    }
                });
                popup.inflate(R.menu.main_menu);
                popup.show();
            }
        });

        // Ask user to download all songs on first app launch
        boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
        if (firstrun) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getString(R.string.downloadAllDialogTitle));
            builder.setMessage(getString(R.string.downloadAllDialogMessage));

            final List<String> finalNameList = new ArrayList<>(nameList);
            builder.setPositiveButton(getString(R.string.downloadAllDialogPositive), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    String internalPath = getApplicationContext().getFilesDir().getAbsolutePath();
                    finalNameList.add(0, internalPath);
                    new DownloadFile(MainActivity.this).execute(finalNameList.toArray(new String[0]));

                    dialog.dismiss();
                }
            });

            builder.setNegativeButton(getString(R.string.downloadAllDialogNegative), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

            // Save the state
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstrun", false)
                    .apply();
        }
    }

    public void onPlayClick(View view) {

        view.startAnimation(new AlphaAnimation(1f, 0.8f));
        // Pause when playing | Play when paused
        mediaPlayerManager.switchPlayPause(view);
    }

    public void onNextClick(View view) {
        mediaPlayerManager.playNextSong();
    }

    public void onPreviousClick(View view) {
        mediaPlayerManager.playPreviousSong();
    }

    public void onShuffleClick(View view) {
        if (mediaPlayerManager.isShuffling()) {
            mediaPlayerManager.setShuffle(false);
            shuffleButton.setColorFilter(null);
        } else {
            shuffleButton.setColorFilter(getResources().getColor(R.color.yellow), android.graphics.PorterDuff.Mode.MULTIPLY);

            mediaPlayerManager.setShuffle(true);
            mediaPlayerManager.setLooping(false);

            repeatButton.setImageResource(R.drawable.repeat_button);
            repeatButton.setColorFilter(null);
        }
    }

    public void onRepeatClick(View view) {
        // loopingSong: true -> Loop entire list
        //              false -> Loop song

        // Reset shuffle button
        shuffleButton.setColorFilter(null);
        mediaPlayerManager.setShuffle(false);

        if (mediaPlayerManager.isLooping()) {
            mediaPlayerManager.setLooping(false);
            repeatButton.setImageResource(R.drawable.repeat_button);
            repeatButton.setColorFilter(null);
        } else {
            mediaPlayerManager.setLooping(true);
            repeatButton.setImageResource(R.drawable.repeat_once_button);
            repeatButton.setColorFilter(getResources().getColor(R.color.yellow), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    public void setAllButtonsEnabled(boolean enabled) {
        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setEnabled(enabled);
        for (int i = 0; i < mainLayout.getChildCount(); i++) {
            View child = mainLayout.getChildAt(i);
            child.setEnabled(enabled);
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}

