package com.daliavi.android.music.app.music;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.SearchView;

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


public class MainActivity extends ActionBarActivity {
    private ListView listViewArtist;
    private ArtistAdapter adapter;
    private ArrayList<ArtistData>  artist_data = new ArrayList<ArtistData>()
    {{
        add(new ArtistData("http://www.oilerie.com/mm5/images/img_no_thumb.jpg", "No artist found","nothing"));
    }};

    public void updateArtist(String itemNumber, String artist){
        FetchArtistTask artistsTask = new FetchArtistTask();
        artistsTask.execute(itemNumber, artist);
    }

    public void onSearchBtnClick(View view){
        EditText edtSearch = (EditText)findViewById(R.id.edtSearch);
        String searchValue = edtSearch.getText().toString();
        updateArtist("10", searchValue);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String LOG_TAG = "ON CREATE";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ArtistAdapter(this, R.layout.listview_item_row, artist_data);
        listViewArtist = (ListView)findViewById(R.id.listViewArtist);
        View header = (View)getLayoutInflater().inflate(R.layout.listview_header_row, null);
        listViewArtist.addHeaderView(header);
        listViewArtist.setAdapter(adapter);
        Log.v(LOG_TAG, "listview was set ");
        updateArtist("10", "boys");

        listViewArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Context context = getApplicationContext();
                //int duration = Toast.LENGTH_SHORT;
                //String text = adapter.getItem(position-1).title.toString();
                String onClickArtistId = artist_data.get(position-1).artist_id;

                //Toast toast = Toast.makeText(context, onClickArtistId, duration);
                //toast.show();

                Intent startDetailActivity = new Intent(getApplicationContext(),TrackListActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, onClickArtistId);
                startActivity(startDetailActivity);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

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
            Toast.makeText(this, "Clicked on Settings", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchArtistTask extends AsyncTask<String, Void, ArrayList<ArtistData>> {

        private final String LOG_TAG = FetchArtistTask.class.getSimpleName();
        private ArrayList<ArtistData> getArtistsDataFromJson(String artistJsonStr, int numItems)
        throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String SPO_ARTISTS = "artists";
            final String SPO_ITEMS = "items"; //Items array
            final String SPO_NAME = "name";
            final String SPO_ARTIST_ID = "id";
            final String SPO_IMAGES = "images"; //Image array
            final String SPO_THUMB_URL = "url";
            artist_data.clear();

            JSONObject artistJson = new JSONObject(artistJsonStr);
            JSONObject artistObject = artistJson.getJSONObject(SPO_ARTISTS);
            JSONArray artistArray = artistObject.getJSONArray(SPO_ITEMS);

            for(int i = 0; i < artistArray.length(); i++) {
                JSONObject artistItemObject = artistArray.getJSONObject(i);
                String artistName = artistItemObject.getString(SPO_NAME);
                String artistId = artistItemObject.getString(SPO_ARTIST_ID);

                JSONArray artistImageArray = artistItemObject.getJSONArray(SPO_IMAGES);

                if (artistImageArray.length() > 0) {
                    JSONObject artistImageURLObject = artistImageArray.getJSONObject(artistImageArray.length() - 1);
                    String artistImageURL = artistImageURLObject.getString(SPO_THUMB_URL);

                    artist_data.add(new ArtistData(artistImageURL, artistName, artistId));
                }
                else {
                    artist_data.add(new ArtistData("http://www.oilerie.com/mm5/images/img_no_thumb.jpg", artistName, "nothing"));
                }
            }
            Log.v(LOG_TAG, "everything is ok ");
            return artist_data;
        }

        @Override
        protected ArrayList<ArtistData> doInBackground(String... params){
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String artistsJsonStr = null;
            String LOG_TAG = "Daliavi-tag";
            String type = "artist";

            try {
                // Construct the URL for the Spotify query

                final String ARTIST_BASE_URL = "https://api.spotify.com/v1/search?";
                final String QUERY_PARAM = "q";
                final String TYPE_PARAM = "type";
                final String LIMIT_PARAM = "limit";

                Uri builtUri = Uri.parse(ARTIST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[1])
                        .appendQueryParameter(TYPE_PARAM, type)
                        .appendQueryParameter(LIMIT_PARAM, params[0])
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

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
                artistsJsonStr = buffer.toString();
                Log.v(LOG_TAG, "JSON String: " + artistsJsonStr);

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
                return getArtistsDataFromJson(artistsJsonStr, 10);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ArtistData> result) {
            super.onPostExecute(result);
            adapter.notifyDataSetChanged();
            if (result != null) {
                Log.v(LOG_TAG, "List update");
            }
        }
    }
}
