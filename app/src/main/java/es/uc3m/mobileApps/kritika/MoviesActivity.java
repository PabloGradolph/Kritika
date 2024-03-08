

package es.uc3m.mobileApps.kritika;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import es.uc3m.mobileApps.kritika.model.Movie;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MoviesActivity extends AppCompatActivity {
    private TextView tvResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        tvResponse = findViewById(R.id.tvResponse);
        new DiscoverMoviesTask().execute();
    }

    private class DiscoverMoviesTask extends AsyncTask<Void, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/popular?language=en-US&page=1")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiOWE1NGY3MzRjMzYxZjFmOWZkNjFiNDUyNDBhMzVmNyIsInN1YiI6IjY1ZDQ5ODc2YmJlMWRkMDE3ZDVmODhmYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.sHPGWnO65YwdqEebPvXIjgBobTKEpE9WufBh9ebal8M") // Replace with your actual access token
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();

                JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
                Type movieListType = new TypeToken<ArrayList<Movie>>(){}.getType();
                List<Movie> movies = new Gson().fromJson(jsonObject.get("results"), movieListType);

                return movies;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            if (movies != null && !movies.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Movie movie : movies) {
                    sb.append(movie.getTitle()).append("\n");
                }
                // Actualizar el TextView con los títulos de películas
                tvResponse.setText(sb.toString());
            } else {
                Toast.makeText(MoviesActivity.this, "Failed to fetch data!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
