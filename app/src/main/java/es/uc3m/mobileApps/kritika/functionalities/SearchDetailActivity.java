package es.uc3m.mobileApps.kritika.functionalities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.io.IOException;

import es.uc3m.mobileApps.kritika.Actions.AddtoListActivity;
import es.uc3m.mobileApps.kritika.Actions.RateActivity;
import es.uc3m.mobileApps.kritika.Actions.ReviewActivity;
import es.uc3m.mobileApps.kritika.Misc.ApiConstants;
import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Movie;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Activity to display details of any type of media.
 */
public class SearchDetailActivity extends AppCompatActivity {

    private FloatingActionButton openMenuButton;
    private String mediaType;
    private int mediaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_movie_item_detail);

        mediaId = Integer.parseInt(getIntent().getStringExtra("id"));
        mediaType = getIntent().getStringExtra("type");

        if (mediaId != -1 && mediaType != null) {
            new FetchMediaDetailsTask().execute(mediaId);
        }

        openMenuButton = findViewById(R.id.openMenuButton);
        openMenuButton.setOnClickListener(view -> showBottomSheetMenu());
    }

    private void showBottomSheetMenu() {
        String mediaIdString = String.valueOf(mediaId);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_menu, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        // Similar to previous activity, handle button clicks here
        bottomSheetView.findViewById(R.id.rateButton).setOnClickListener(v -> startActivity(new Intent(this, RateActivity.class).putExtra("mediaId", mediaIdString).putExtra("mediaType", mediaType)));
        bottomSheetView.findViewById(R.id.addToListButton).setOnClickListener(v -> startActivity(new Intent(this, AddtoListActivity.class).putExtra("mediaId", mediaIdString).putExtra("mediaType", mediaType)));
        bottomSheetView.findViewById(R.id.reviewButton).setOnClickListener(v -> startActivity(new Intent(this, ReviewActivity.class).putExtra("mediaId", mediaIdString).putExtra("mediaType", mediaType)));
    }

    private class FetchMediaDetailsTask extends AsyncTask<Integer, Void, Movie> {
        @Override
        protected Movie doInBackground(Integer... mediaIds) {
            final OkHttpClient client = new OkHttpClient();
            String baseUrl = ApiConstants.MOVIEDB_BASE_URL;
            String token = ApiConstants.MOVIEDB_ACCESS_TOKEN;
            final Request request = new Request.Builder()
                    .url(baseUrl + mediaIds[0])
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String jsonData = response.body().string();
                JSONObject movieJson = new JSONObject(jsonData);
                Log.d("MovieJson", String.valueOf(movieJson));
                return new Movie(
                        movieJson.getInt("id"),
                        movieJson.getString("title"),
                        movieJson.getString("overview"),
                        movieJson.getString("poster_path"),
                        movieJson.getString("vote_average"),
                        movieJson.getString("release_date"),
                        "movies"
                );

            } catch (Exception e) {
                Log.e("MediaDetail", "Error fetching media details", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie media) {
            super.onPostExecute(media);
            if (media != null) {
                TextView tvTitle = findViewById(R.id.tvTitle);
                TextView tvDescription = findViewById(R.id.tvDescription); // Use a generic ID
                ImageView mediaImageView = findViewById(R.id.mediaImageViewPoster);

                tvTitle.setText(media.getTitle());
                tvDescription.setText(media.getOverview()); // Assume getDescription handles different types
                Glide.with(SearchDetailActivity.this)
                        .load(media.getImagePath())
                        .into(mediaImageView);
            } else {
                Toast.makeText(SearchDetailActivity.this, "Error loading media details.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
