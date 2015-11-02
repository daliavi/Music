package com.daliavi.android.music.app.music;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class TrackListActivity extends ActionBarActivity {
    private ListView listViewTracks;
    private TrackAdapter adapter;
    private ArrayList<TrackData> track_data = new ArrayList<TrackData>()
    {{
            add(new TrackData("http://www.oilerie.com/mm5/images/img_no_thumb.jpg", "No title", "No Album"));
        }};


    public void updateTrackList(String itemNumber, String artist){
        FetchTrackDataTask trackTask = new FetchTrackDataTask();
        trackTask.execute(itemNumber, artist);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        adapter = new TrackAdapter(this, R.layout.listview_track_detail_row, track_data);
        listViewTracks = (ListView)findViewById(R.id.listViewTracks);
        listViewTracks.setAdapter(adapter);

        Intent intent = this.getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            String intent_value = intent.getStringExtra(Intent.EXTRA_TEXT);

            Context context = this;
            int duration = Toast.LENGTH_SHORT;


            Toast toast = Toast.makeText(context, intent_value + " from Intent Activity", duration);
            toast.show();

            updateTrackList("10", intent_value);

        } else {
            updateTrackList("10", "3qm84nBOXUEQ2vnTfUTTFC");

        }



        //Log.v(LOG_TAG, "listview was set ");
        //updateArtist("10", "boys");

       // Context context = this;
       // int duration = Toast.LENGTH_LONG;


       // Toast toast = Toast.makeText(context, ArtistData.getInstance().getTitle() + " from singleton Activity", duration);
       // toast.show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //download track details for the selected artist

    public class FetchTrackDataTask extends AsyncTask<String, Void, ArrayList<TrackData>> {

        private final String LOG_TAG = FetchTrackDataTask.class.getSimpleName();
        private ArrayList<TrackData> getTrackDataFromJson(String trackJsonStr, int numItems)
                throws JSONException {



            // These are the names of the JSON objects that need to be extracted.
            final String SPO_ARTISTS = "artists";
            final String SPO_ITEMS = "items"; //Items array
            final String SPO_NAME = "name";
            final String SPO_IMAGES = "images"; //Image array
            final String SPO_THUMB_URL = "url";

            // constants for track parsing
            final  String SPO_TRACK = "tracks";
            final String SPO_TRACK_NAME = "name";
            track_data.clear();

            JSONObject trackJson = new JSONObject(trackJsonStr);
            JSONArray trackArray =trackJson.getJSONArray(SPO_TRACK);

            for(int i = 0; i < trackArray.length(); i++) {
                JSONObject trackObject = trackArray.getJSONObject(i);
                String trackName = trackObject.getString(SPO_TRACK_NAME);
                JSONObject albumObject = trackObject.getJSONObject("album");
                String albumName = albumObject.getString("name");

                JSONArray albumImageArray = albumObject.getJSONArray(SPO_IMAGES);

                if (albumImageArray.length() > 0) {
                    JSONObject albumImageURLObject = albumImageArray.getJSONObject(albumImageArray.length() - 1);
                    String albumImageURL = albumImageURLObject.getString(SPO_THUMB_URL);

                    track_data.add(new TrackData(albumImageURL, trackName, albumName));
                    Log.v(LOG_TAG, "album image url " + albumImageURL);
                }
                else {
                    track_data.add(new TrackData("http://www.oilerie.com/mm5/images/img_no_thumb.jpg", trackName, albumName));
                    Log.v(LOG_TAG, "album image url NOT FOUND");
                }

                Log.v(LOG_TAG, "track name: " + trackName + " album: " + albumName);
            }


            Log.v(LOG_TAG, "everything is ok ");
            return track_data;
        }

        @Override
        protected ArrayList<TrackData> doInBackground(String... params){
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String trackJsonStr = null;
            String LOG_TAG = "Daliavi-tag";
            String type = "artist";

            try {
                // Construct the URL for the Spotify query

                final String TOP_TRACKS_BASE_URL = "https://api.spotify.com/v1/artists/";
                final String TOP_TRACKS_BASE_URL_END = "top-tracks";
                final String TRACK_COUNTRY_VALUE = "US";
                final String TRACK_COUNTY_PARAM = "country";

                Uri builtUri = Uri.parse(TOP_TRACKS_BASE_URL).buildUpon()
                        .appendPath(params[1])
                        .appendPath(TOP_TRACKS_BASE_URL_END)
                        .appendQueryParameter(TRACK_COUNTY_PARAM, TRACK_COUNTRY_VALUE)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI Track" + builtUri.toString());

                // Create the request to Spotify, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // print out the completed buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                trackJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Track JSON String: " + trackJsonStr);

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the artist data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                return getTrackDataFromJson(trackJsonStr, 10);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<TrackData> result) {
            super.onPostExecute(result);
            adapter.notifyDataSetChanged();
            if (result != null) {
                Log.v(LOG_TAG, "List update");
            }
        }
    }

}
