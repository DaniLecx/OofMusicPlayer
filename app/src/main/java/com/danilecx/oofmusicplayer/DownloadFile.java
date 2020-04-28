package com.danilecx.oofmusicplayer;

import android.os.AsyncTask;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFile extends AsyncTask<String, Integer, String> {

    private String internalPath;
    private int nbParams;
    private WeakReference<MainActivity> activityReference;

    DownloadFile(MainActivity context) {
        activityReference = new WeakReference<>(context);

    }

    // Download X songs in parameter
    @Override
    protected String doInBackground(String... urlParams) {
        final MainActivity activity = activityReference.get();
        if (!(activity == null || activity.isFinishing())) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    activity.setAllButtonsEnabled(true);
                }
            });
        }

        int count;
        String baseURL = "https://raw.githubusercontent.com/DaniLecx/OofMusicPlayer/master/assets/";
        nbParams = urlParams.length;
        try {
            for (int nbSong = 1; nbSong < nbParams; nbSong++) {
                URL url = new URL(baseURL + urlParams[nbSong] + ".mp3");
                URLConnection connection = url.openConnection();
                connection.connect();
                // Get file size, useful for progress calculation
                int lengthOfFile = connection.getContentLength();

                // Download the file
                internalPath = urlParams[0] + "/" + urlParams[nbSong] + ".mp3";
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(internalPath);

                byte[] data = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // Publishing the progress...
                    publishProgress(nbSong, nbParams - 1, (int) (total * 100 / lengthOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Show progress on seekbar
    @Override
    protected void onProgressUpdate(Integer... progress) {
        MainActivity activity = activityReference.get();
        if (activity == null || activity.isFinishing()) return;

        SeekBar seekBar = activity.findViewById(R.id.seekBar);
        TextView songNameTV = activity.findViewById(R.id.songTextView);

        seekBar.setMax(100);
        seekBar.setProgress(progress[2]);
        songNameTV.setText(activity.getString(R.string.downloading, progress[0], progress[1], progress[2]));
        //System.out.println(seekBar.getProgress());
    }

    // Re-enable all buttons
    @Override
    protected void onPostExecute(String s) {
        final MainActivity activity = activityReference.get();
        if (activity == null || activity.isFinishing()) return;

        activity.runOnUiThread(new Runnable() {
            public void run() {
                activity.setAllButtonsEnabled(true);
            }
        });


        if (nbParams <= 2) {
            MediaPlayerManager mediaPlayerManager = activity.mediaPlayerManager;
            mediaPlayerManager.prepare(internalPath);
        } else {
            TextView songNameTV = activity.findViewById(R.id.songTextView);
            songNameTV.setText("");
        }
    }
}
