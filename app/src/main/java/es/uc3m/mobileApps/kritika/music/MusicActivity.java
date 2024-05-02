package es.uc3m.mobileApps.kritika.music;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.uc3m.mobileApps.kritika.DashboardUserActivity;
import es.uc3m.mobileApps.kritika.MainActivity;
import es.uc3m.mobileApps.kritika.Misc.ApiConstants;
import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Song;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Activity to display music-related content.
 */
public class MusicActivity extends DashboardUserActivity {

    private RecyclerView rvSongs;
    private SongsAdapter adapter;
    private List<Song> songsList = new ArrayList<>();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        rvSongs = findViewById(R.id.rvSongs);
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongsAdapter(this, songsList);
        adapter.setOnItemClickListener(new SongsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song song) {
                Intent intent = new Intent(MusicActivity.this, MusicDetailActivity.class);
                intent.putExtra("name", song.getName());
                intent.putExtra("id", song.getId());
                startActivity(intent);
            }
        });
        rvSongs.setAdapter(adapter);

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        ImageButton buttonLogOut = findViewById(R.id.logoutBtn);
        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        // TOP BAR: buttons
        Button buttonOpenMovies = findViewById(R.id.button_open_movies);
        Button buttonOpenMusic = findViewById(R.id.button_open_music);
        Button buttonOpenBooks = findViewById(R.id.button_open_books);
        Button buttonOpenReviews = findViewById(R.id.button_open_reviews);

        // BOTTOM BAR: buttons
        ImageButton buttonOpenProfile = findViewById(R.id.profileButton);
        ImageButton buttonOpenHome = findViewById(R.id.houseButton);
        ImageButton buttonOpenSearch = findViewById(R.id.searchButton);

        // Set click listeners for buttons
        setButtonListeners(buttonOpenMovies, buttonOpenMusic, buttonOpenBooks, buttonOpenReviews, buttonOpenProfile,
                buttonOpenHome, buttonOpenSearch);
        new MusicActivity.DiscoverSongsTask().execute();
    }

    /**
     * AsyncTask to fetch and display songs.
     */
    private class DiscoverSongsTask extends AsyncTask<Void, Void, List<Song>> {

        @Override
        protected List<Song> doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            List<Song> songs = new ArrayList<>();

            // URL to request client access token
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

                String topTracksUrl = ApiConstants.SPOTIFY_NEWRELEASES_URL;

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

                    Song song = new Song(trackId, trackName, artistName, trackUrl, imageUrl, "songs");
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

                // Save songs to Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference songsRef = db.collection("songs");

                for (Song song: songs) {
                    // Check if the document exists in Firestore based on its ID
                    DocumentReference docRef = songsRef.document(String.valueOf(song.getId()));
                    docRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (!document.exists()) {
                                songsRef.document(String.valueOf(song.getId())).set(song);
                            }
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                        }
                    });
                }
            } else {
                Toast.makeText(MusicActivity.this, "Failed to fetch data!", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Check if the user is logged in.
     */
    private void checkUser() {
        // get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            // not logged in, go to main screen
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // logged in, get user info
            String UserName = firebaseUser.getEmail();
            // set in textview of toolbar
            TextView subTitleTv = findViewById(R.id.subTitleTv);
            subTitleTv.setText(UserName);

        }
    }
}