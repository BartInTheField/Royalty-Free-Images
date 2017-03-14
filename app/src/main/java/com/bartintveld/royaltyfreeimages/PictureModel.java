package com.bartintveld.royaltyfreeimages;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Bart on 8-3-2017.
 */

public class PictureModel extends AsyncTask<String, Void, String> {

    private PicturesAvailable listener = null;

    private static final String TAG = "AsyncSpotify";

    public PictureModel(PicturesAvailable listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        //Log.i(TAG, "doInBackground");
        InputStream inputStream = null;
        int responsCode = -1;
        // De URL die we via de .execute() meegeleverd krijgen
        String personUrl = params[0];
        String result = "";

        //Log.i(TAG, "doInBackground - " + personUrl);
        try {
            // Maak een URL object
            URL url = new URL(personUrl);
            // Open een connection op de URL
            URLConnection urlConnection = url.openConnection();

            if (!(urlConnection instanceof HttpURLConnection)) {
                return null;
            }

            // Initialiseer een HTTP connectie
            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setInstanceFollowRedirects(true);
            httpConnection.setRequestMethod("GET");

            // Voer het request uit via de HTTP connectie op de URL
            httpConnection.connect();
            // Kijk of het gelukt is door de response code te checken
            responsCode = httpConnection.getResponseCode();
            if (responsCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpConnection.getInputStream();
                result = getStringFromInputStream(inputStream);
            } else {
                //Log.e(TAG, "Error, invalid response");
            }
        } catch (MalformedURLException e) {
            //Log.e(TAG, "doInBackground MalformedURLEx " + e.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            //Log.e("TAG", "doInBackground IOException " + e.getLocalizedMessage());
            return null;
        }
        return result;
    }


    /**
     * onPostExecute verwerkt het resultaat uit de doInBackground methode.
     *
     * @param result
     */

    @Override
    protected void onPostExecute(String result) {

        try {
            //Top level object
            JSONObject jsonObject = new JSONObject(result);

            // Getting all albums and start looping
            JSONArray hits = jsonObject.getJSONArray("hits");

            ArrayList<Picture> pictures = new ArrayList<>();

            for (int idx = 0; idx < hits.length(); idx++) {

                //Array level objects and albums
                JSONObject hit = hits.getJSONObject(idx);

                // Get albumName, albumID
                String pictureName = hit.getString("tags");
                String creator = hit.getString("user");

                //Get image urls.
                String imageThumbURL = hit.getString("previewURL");
                String imageURL = hit.getString("webformatURL");

                //Create new Picture
                Picture s = new Picture(pictureName, imageURL, imageThumbURL, creator);

                //Add SpotifyAlbum in list
                pictures.add(s);


            }
            //Callback
            listener.pictureAvailable(pictures);
        } catch (JSONException ex) {
            //Log.e(TAG, "onPostExecute JSONException " + ex.getLocalizedMessage());

        }


    }

    //
    // convert InputStream to String
    //
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    // Call back interface
    public interface PicturesAvailable {
        void pictureAvailable(ArrayList<Picture> result);
    }
}
