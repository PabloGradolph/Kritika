package es.uc3m.mobileApps.kritika.newDashboard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Song;
import es.uc3m.mobileApps.kritika.Misc.ApiConstants;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Fragment to display a list of songs.
 */
public class newMusicFragment extends Fragment {
    private RecyclerView rvSongs;
    private newSongsAdapter adapter;
    private List<Song> songsList = new ArrayList<>();

    // Empty constructor required
    public newMusicFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.new_fragment_music, container, false);

        rvSongs = view.findViewById(R.id.rvSongs);
        rvSongs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new newSongsAdapter(getContext(), songsList);
        adapter.setOnItemClickListener(new newSongsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song song) {
                Intent intent = new Intent(getActivity(), newMusicDetailActivity.class);
                intent.putExtra("name", song.getName());
                intent.putExtra("id", song.getId());
                startActivity(intent);
            }
        });
        rvSongs.setAdapter(adapter);

        new RetrieveSongsTask().execute();
        return view;
    }

    /**
     * AsyncTask to retrieve songs from the Spotify API.
     */
    private class RetrieveSongsTask extends AsyncTask<Void, Void, List<Song>> {

        @Override
        protected List<Song> doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            List<Song> songs = new ArrayList<>();

            // URL for requesting a client access token
            String tokenUrl = ApiConstants.SPOTIFY_TOKEN_URL;

            // Use Base64 to encode Client ID and Client Secret
            String credentials = Base64.encodeToString((ApiConstants.S_CLIENT_ID + ":" + ApiConstants.S_CLIENT_SECRET).getBytes(), Base64.NO_WRAP);

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

                String topTracksUrl = ApiConstants.SPOTIFY_PLAYLIST_URL;

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

                    String trackId = track.getString("id");
                    String trackName = track.getString("name");

                    JSONArray artists = track.getJSONArray("artists");
                    String artistName = artists.getJSONObject(0).getString("name");

                    String trackUrl = track.getJSONObject("external_urls").getString("spotify");
                    String imageUrl = track.getJSONObject("album").getJSONArray("images").getJSONObject(1).getString("url");

                    Song song = new Song(trackId, trackName, artistName, trackUrl, imageUrl);
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
            }
        }
    }
}