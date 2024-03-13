
package es.uc3m.mobileApps.kritika;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.uc3m.mobileApps.kritika.model.Movie;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MoviesActivity extends AppCompatActivity {
    private RecyclerView rvMovies;
    private MoviesAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        rvMovies = findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MoviesAdapter(this, movieList);
        rvMovies.setAdapter(adapter);

        new DiscoverMoviesTask().execute();
    }

    private class DiscoverMoviesTask extends AsyncTask<Void, Void, List<Movie>> {
        @Override
        protected List<Movie> doInBackground(Void... voids) {
            // Crear el interceptor de logging y configurar el nivel de log
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Construir el cliente OkHttpClient e incluir el interceptor de logging
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/popular?language=en-US&page=1")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1YzBmM2FmNjcyNjM5YTJjZmUyNmY4NDMyMjk5NjNmNCIsInN1YiI6IjY1ZDg5ZjZiMTQ5NTY1MDE2MmY1YTZhNCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.Io4x374YopHoiG57NIBLZEroKn2vInK1Dzfddkp-ECE")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray results = jsonObject.getJSONArray("results");
                List<Movie> movies = new ArrayList<>();
                for (int i = 0; i < results.length(); i++) {
                    JSONObject movieJson = results.getJSONObject(i);
                    Movie movie = new Movie(
                            movieJson.getInt("id"),
                            movieJson.getString("title"),
                            movieJson.getString("overview"),
                            movieJson.getString("poster_path"),
                            movieJson.getDouble("vote_average"),
                            movieJson.getString("release_date")
                    );
                    movies.add(movie);
                }
                return movies;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            if (movies != null) {
                movieList.clear();
                movieList.addAll(movies);
                adapter.notifyDataSetChanged();
            } else {
                // Mostrar mensaje de error
            }
        }
    }
}