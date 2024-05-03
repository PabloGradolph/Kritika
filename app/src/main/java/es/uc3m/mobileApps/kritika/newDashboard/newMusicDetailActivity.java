package es.uc3m.mobileApps.kritika.newDashboard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.List;

import es.uc3m.mobileApps.kritika.Actions.AddtoListActivity;
import es.uc3m.mobileApps.kritika.Actions.RateActivity;
import es.uc3m.mobileApps.kritika.Actions.ReviewActivity;
import es.uc3m.mobileApps.kritika.Misc.ApiConstants;
import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Song;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Activity to display details of a music track.
 */
public class newMusicDetailActivity extends AppCompatActivity {

    private FloatingActionButton openMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_music_item_detail);

        String songId = getIntent().getStringExtra("id");

        Log.d("songId", songId);

        if (songId != null) {
            fetchSongFromDB(songId);
        } else {
            Toast.makeText(this, "Song name not provided", Toast.LENGTH_SHORT).show();
        }



        openMenuButton = findViewById(R.id.openMenuButton);
        openMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetMenu();
            }
        });
    }

    /**
     * Show the bottom sheet menu for rating, adding to list, and reviewing.
     */
    private void showBottomSheetMenu() {
        String trackId = getIntent().getStringExtra("id");
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_menu, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        bottomSheetView.findViewById(R.id.rateButton).setOnClickListener(v -> {
            // Start RateActivity
            Intent intent = new Intent(this, RateActivity.class);
            intent.putExtra("mediaId", trackId);
            intent.putExtra("mediaType", "songs");
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.addToListButton).setOnClickListener(v -> {
            // Start AddToListActivity
            Intent intent = new Intent(this, AddtoListActivity.class);
            intent.putExtra("mediaId", trackId);
            intent.putExtra("mediaType", "songs");
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.reviewButton).setOnClickListener(v -> {
            // Start ReviewActivity
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("mediaId", trackId);
            intent.putExtra("mediaType", "songs");
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
    }

    /**
     * AsyncTask to fetch music details from Spotify API.
     */
    private class FetchMusicDetailsTask extends AsyncTask<String, Void, Song> {
        @Override
        protected Song doInBackground(String... songIds) {
            final OkHttpClient client = new OkHttpClient();
            String tokenUrl = ApiConstants.SPOTIFY_TOKEN_URL;
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

                String searchUrl = ApiConstants.SPOTIFY_TRACKS_URL + songIds[0];

                Request tracksRequest = new Request.Builder()
                        .url(searchUrl)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();

                Response trackResponse = client.newCall(tracksRequest).execute();
                String trackjsonData = trackResponse.body().string();
                JSONObject track = new JSONObject(trackjsonData);

                // Extract relevant details like name, artist, URL, and image URL
                String trackId = track.getString("id");
                String name = track.getString("name");
                String artistName = track.getJSONArray("artists").getJSONObject(0).getString("name");
                String url = track.getJSONObject("external_urls").getString("spotify");
                String imageUrl = track.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");

                return new Song(trackId, name, artistName, url, imageUrl, "songs");

            } catch (Exception e) {
                Log.e("SongDetail", "Error fetching song details", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Song song) {
            super.onPostExecute(song);
            if (song != null) {
                TextView musicTitle = findViewById(R.id.musicTitle);
                TextView musicArtist = findViewById(R.id.musicArtist);
                ImageView imageViewPoster = findViewById(R.id.imageViewPoster);

                musicTitle.setText(song.getName());
                musicArtist.setText(song.getArtistName());

                String posterUrl = song.getImageUrl();
                Glide.with(newMusicDetailActivity.this)
                        .load(posterUrl)
                        .into(imageViewPoster);
            } else {
                Toast.makeText(newMusicDetailActivity.this, "Failed to fetch song details.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * AsyncTask to fetch details of the song from the DB.
     */
    private void fetchSongFromDB(String songId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("songs").document(songId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {

                    if (document.contains("image")) {
                        updateUIWithSongDetails(document);
                    } else {
                        new newMusicDetailActivity.FetchMusicDetailsTask().execute(songId);
                    }
                    //
                    Log.d("document", String.valueOf(document));
                } else {
                    Log.e("SongDetail", "No such document");
                    Toast.makeText(newMusicDetailActivity.this, "No movie details found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("SongDetail", "Error fetching song details", task.getException());
                Toast.makeText(newMusicDetailActivity.this, "Error loading song details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIWithSongDetails(DocumentSnapshot song) {
        TextView musicTitle = findViewById(R.id.musicTitle);
        TextView musicArtist = findViewById(R.id.musicArtist);
        ImageView imageViewPoster = findViewById(R.id.imageViewPoster);

        musicTitle.setText(song.getString("title"));

        List<String> artists = (List<String>) song.get("artists");
        String artistsText = String.join(", ", artists);
        musicArtist.setText(artistsText);

        String posterUrl = song.getString("image");
        Glide.with(this)
                .load(posterUrl)
                .into(imageViewPoster);
    }
}
