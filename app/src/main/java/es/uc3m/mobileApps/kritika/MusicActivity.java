package es.uc3m.mobileApps.kritika;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.uc3m.mobileApps.kritika.model.Movie;
import es.uc3m.mobileApps.kritika.model.Song;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MusicActivity extends AppCompatActivity {

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

        new MusicActivity.DiscoverSongsTask().execute();
    }
    //Comment testea si funciona esta llamada a la API a la que tengas WIFI
    // KEY: 96f13ee809056c77d25defbd0f813024
    private class DiscoverSongsTask extends AsyncTask<Void, Void, List<Song>> {

        @Override
        protected List<Song> doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            List<Song> songs = new ArrayList<>();
            String url = "https://ws.audioscrobbler.com/2.0/?method=chart.gettoptracks&api_key=96f13ee809056c77d25defbd0f813024&format=json";

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray tracks = jsonObject.getJSONObject("tracks").getJSONArray("track");

                for (int i = 0; i < tracks.length(); i++) {
                    JSONObject trackJson = tracks.getJSONObject(i);
                    JSONObject artistJson = trackJson.getJSONObject("artist");
                    String imageUrl = trackJson.getJSONArray("image").getJSONObject(2).getString("#text"); // using large image

                    Song song = new Song(
                            trackJson.getString("name"),
                            artistJson.getString("name"),
                            trackJson.getString("url"),
                            imageUrl
                    );
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