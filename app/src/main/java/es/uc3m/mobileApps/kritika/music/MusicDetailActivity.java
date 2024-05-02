package es.uc3m.mobileApps.kritika.music;

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

import org.json.JSONObject;

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
 * This activity displays details of a music item.
 */
public class MusicDetailActivity extends AppCompatActivity {

    private FloatingActionButton openMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_item_detail);

        String songId = getIntent().getStringExtra("id");
        Log.d("songId", songId);

        if (songId != null) {
            new MusicDetailActivity.FetchMusicDetailsTask().execute(songId);
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
     * Displays the bottom sheet menu.
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
     * AsyncTask to fetch details of a music item from Spotify API.
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

                String trackUrl = ApiConstants.SPOTIFY_TRACKS_URL + songIds[0];

                Request tracksRequest = new Request.Builder()
                        .url(trackUrl)
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
                Log.e("MusicDetail", "Error fetching music details", e);
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
                Glide.with(MusicDetailActivity.this)
                        .load(posterUrl)
                        .into(imageViewPoster);
            } else {
                Toast.makeText(MusicDetailActivity.this, "Failed to fetch song details.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
