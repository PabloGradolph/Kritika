package es.uc3m.mobileApps.kritika.newDashboard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.uc3m.mobileApps.kritika.DashboardUserActivity;
import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Song;
import es.uc3m.mobileApps.kritika.music.SongsAdapter;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class newMusicActivity extends DashboardUserActivity {

    private RecyclerView rvSongs;
    private SongsAdapter adapter;
    private List<Song> songsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_dashboard_user);

        rvSongs = findViewById(R.id.rvSongs);
        rvSongs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new SongsAdapter(this, songsList);
        rvSongs.setAdapter(adapter);

        Button buttonOpenMovies = findViewById(R.id.button_open_movies);
        Button buttonOpenMusic = findViewById(R.id.button_open_music);
        Button buttonOpenBooks = findViewById(R.id.button_open_books);
        Button buttonOpenNew = findViewById(R.id.button_open_new); // NEW BUTTON
        ImageButton buttonOpenProfile = findViewById(R.id.profileButton);
        ImageButton buttonOpenHome = findViewById(R.id.houseButton);
        ImageButton buttonOpenSearch = findViewById(R.id.searchButton);

        // Set click listeners for buttons
        setButtonListeners(buttonOpenMovies, buttonOpenMusic, buttonOpenBooks, buttonOpenNew, buttonOpenProfile,
                buttonOpenHome, buttonOpenSearch);
        new newMusicActivity.DiscoverSongsTask().execute();
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
                Toast.makeText(newMusicActivity.this, "Failed to fetch data!", Toast.LENGTH_LONG).show();
            }
        }
    }
}