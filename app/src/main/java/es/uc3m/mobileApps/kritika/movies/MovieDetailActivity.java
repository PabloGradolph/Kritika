package es.uc3m.mobileApps.kritika.movies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Movie;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MovieDetailActivity extends AppCompatActivity {

    // ... tus variables de vistas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_item_detail);

        int movieId = getIntent().getIntExtra("id", -1);

        if (movieId != -1) {
            new FetchMovieDetailsTask().execute(movieId);
        } else {
            // Manejar el caso de que no se encuentre un ID válido
        }
    }

    private class FetchMovieDetailsTask extends AsyncTask<Integer, Void, Movie> {
        @Override
        protected Movie doInBackground(Integer... movieIds) {
            final OkHttpClient client = new OkHttpClient();
            String baseUrl = "https://api.themoviedb.org/3/movie/";
            String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1YzBmM2FmNjcyNjM5YTJjZmUyNmY4NDMyMjk5NjNmNCIsInN1YiI6IjY1ZDg5ZjZiMTQ5NTY1MDE2MmY1YTZhNCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.Io4x374YopHoiG57NIBLZEroKn2vInK1Dzfddkp-ECE";
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

                // Asegúrate de que estos nombres de campo coinciden con la respuesta de la API.
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
                ImageView imageViewPoster = findViewById(R.id.imageViewPoster);

                tvTitle.setText(movie.getTitle());
                tvOverview.setText(movie.getOverview());
                tvRating.setText(String.format(Locale.getDefault(), "Rating: %s", movie.getRating()));

                // Asegúrate de que la URL del póster sea completa y válida
                String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
                Glide.with(MovieDetailActivity.this)
                        .load(posterUrl)
                        .into(imageViewPoster);
            } else {
                Toast.makeText(MovieDetailActivity.this, "Error al cargar los detalles de la película.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}