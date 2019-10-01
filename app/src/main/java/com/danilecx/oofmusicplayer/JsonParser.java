package com.danilecx.oofmusicplayer;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JsonParser extends AsyncTask<String, Integer, List<String>> {

    private String getJSONFromUrl(String urlString) {

        StringBuilder fullString = new StringBuilder();
        try {
            URL url = new URL(urlString);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                fullString.append(line);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullString.toString();

    }

    private List<String> getNamesFromJSON(String jsonString) {
        List<String> nameList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                String fileName = jsonArray.getJSONObject(i).getString("name");
                nameList.add(fileName.substring(0, fileName.length() - 4));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nameList;

    }

    @Override
    protected List<String> doInBackground(String... urlString) {
        String jsonString = getJSONFromUrl(urlString[0]);
        return getNamesFromJSON(jsonString);
    }
}
