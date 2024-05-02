package es.uc3m.mobileApps.kritika.newDashboard;

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
import java.util.Locale;

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
 * Activity to display details of a movie.
 */
public class NewMoviesDetailActivity extends AppCompatActivity {

    private FloatingActionButton openMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_movie_item_detail);

        int movieId = getIntent().getIntExtra("id", -1);

        if (movieId != -1) {
            new FetchMovieDetailsTask().execute(movieId);
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
     * Show the bottom sheet menu for additional actions.
     */
    private void showBottomSheetMenu() {
        int movieId = getIntent().getIntExtra("id", 0);
        String movieIdString = String.valueOf(movieId);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_menu, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        bottomSheetView.findViewById(R.id.rateButton).setOnClickListener(v -> {
            // Start RateActivity
            Intent intent = new Intent(this, RateActivity.class);
            intent.putExtra("mediaId", movieIdString);
            intent.putExtra("mediaType", "movies");
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.addToListButton).setOnClickListener(v -> {
            // Start AddToListActivity
            Intent intent = new Intent(this, AddtoListActivity.class);
            intent.putExtra("mediaId", movieIdString);
            intent.putExtra("mediaType", "movies");
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.reviewButton).setOnClickListener(v -> {
            // Start ReviewActivity
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("mediaId", movieIdString);
            intent.putExtra("mediaType", "movies");
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
    }

    /**
     * AsyncTask to fetch details of the movie from an API.
     */
    private class FetchMovieDetailsTask extends AsyncTask<Integer, Void, Movie> {
        @Override
        protected Movie doInBackground(Integer... movieIds) {
            final OkHttpClient client = new OkHttpClient();
            String baseUrl = ApiConstants.MOVIEDB_BASE_URL;
            String token = ApiConstants.MOVIEDB_ACCESS_TOKEN;
            final Request request = new Request.Builder()
                    .url(baseUrl + movieIds[0])
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", token)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String jsonData = response.body().string();
                JSONObject movieJson = new JSONObject(jsonData);

                return new Movie(
                        movieJson.getInt("id"),
                        movieJson.getString("title"),
                        movieJson.getString("overview"),
                        movieJson.getString("poster_path"),
                        movieJson.getString("vote_average"),
                        movieJson.getString("release_date")
                );

            } catch (Exception e) {
                Log.e("MovieDetail", "Error fetching movie details", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie movie) {
            super.onPostExecute(movie);
            if (movie != null) {
                TextView tvTitle = findViewById(R.id.tvTitle);
                TextView tvOverview = findViewById(R.id.tvOverview);
                TextView tvRating = findViewById(R.id.tvRating);
                ImageView movieImageViewPoster = findViewById(R.id.movieImageViewPoster);

                tvTitle.setText(movie.getTitle());
                tvOverview.setText(movie.getOverview());
                tvRating.setText(String.format(Locale.getDefault(), "Rating: %s", movie.getRating()));

                String posterUrl = ApiConstants.MOVIEDB_IMAGE_URL + movie.getPosterPath();
                Glide.with(NewMoviesDetailActivity.this)
                        .load(posterUrl)
                        .into(movieImageViewPoster);
            } else {
                Toast.makeText(NewMoviesDetailActivity.this, "Error al cargar los detalles de la película.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
