package es.uc3m.mobileApps.kritika;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.uc3m.mobileApps.kritika.model.Movie;
import es.uc3m.mobileApps.kritika.model.Song;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MusicActivity extends DashboardUserActivity {

    private RecyclerView rvSongs;
    private SongsAdapter adapter;
    private List<Song> songsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        rvSongs = findViewById(R.id.rvSongs);
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongsAdapter(this, songsList);
        rvSongs.setAdapter(adapter);

        Button buttonOpenMovies = findViewById(R.id.button_open_movies);
        Button buttonOpenMusic = findViewById(R.id.button_open_music);
        Button buttonOpenBooks = findViewById(R.id.button_open_books);

        // Set click listeners for buttons
        setButtonListeners(buttonOpenMovies, buttonOpenMusic, buttonOpenBooks);

        new MusicActivity.DiscoverSongsTask().execute();
    }
    //Comment testea si funciona esta llamada a la API a la que tengas WIFI
    // KEY: 96f13ee809056c77d25defbd0f813024
    private class DiscoverSongsTask extends AsyncTask<Void, Void, List<Song>> {

        @Override
        protected List<Song> doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            List<Song> songs = new ArrayList<>();

            // Esta URL es para pedir un token de acceso de cliente
            String tokenUrl = "https://accounts.spotify.com/api/token";

            // Usa Base64 para codificar Client ID y Client Secret
            String credentials = Base64.encodeToString(("904e4d28994c4a70963a2fb5b5744729:fb988307f5fd400fb34e8400fd557ca8").getBytes(), Base64.NO_WRAP);

            Request tokenRequest = new Request.Builder()
                    .url(tokenUrl)
                    .post(new FormBody.Builder().add("grant_type", "client_credentials").build())
                    .addHeader("Authorization", "Basic " + credentials)
                    .build();

            try {
                Response tokenResponse = client.newCall(tokenRequest).execute();
                String jsonData = tokenResponse.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);
                String accessToken = jsonObject.getString("access_token");

                String topTracksUrl = "https://api.spotify.com/v1/playlists/37i9dQZEVXbMDoHDwVN2tF/tracks";

                Request tracksRequest = new Request.Builder()
                        .url(topTracksUrl)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();

                Response tracksResponse = client.newCall(tracksRequest).execute();
                String tracksjsonData = tracksResponse.body().string();
                JSONObject topTracksjsonObject = new JSONObject(tracksjsonData);
                JSONArray tracks = topTracksjsonObject.getJSONArray("items");

                for (int i = 0; i < tracks.length(); i++) {
                    JSONObject trackItem = tracks.getJSONObject(i);
                    JSONObject track = trackItem.getJSONObject("track");
                    String trackName = track.getString("name");

                    JSONArray artists = track.getJSONArray("artists");
                    String artistName = artists.getJSONObject(0).getString("name");

                    String trackUrl = track.getJSONObject("external_urls").getString("spotify");
                    String imageUrl = track.getJSONObject("album").getJSONArray("images").getJSONObject(1).getString("url");

                    Song song = new Song(trackName, artistName, trackUrl, imageUrl);
                    songs.add(song);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return songs;
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            super.onPostExecute(songs);
            if (songs != null) {
                songsList.clear();
                songsList.addAll(songs);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MusicActivity.this, "Failed to fetch data!", Toast.LENGTH_LONG).show();
            }
        }
    }
}