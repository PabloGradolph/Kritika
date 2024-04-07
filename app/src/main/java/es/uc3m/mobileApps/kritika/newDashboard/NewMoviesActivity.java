
package es.uc3m.mobileApps.kritika.newDashboard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.uc3m.mobileApps.kritika.DashboardUserActivity;
import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Movie;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class NewMoviesActivity extends DashboardUserActivity {
    private RecyclerView rvMovies;
    private NewMoviesAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_dashboard_user);

        rvMovies = findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new NewMoviesAdapter(this, movieList);
        // listener de clic en tu adaptador
        adapter.setOnItemClickListener(new NewMoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Intent intent = new Intent(NewMoviesActivity.this, NewMoviesDetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
        rvMovies.setAdapter(adapter);

        Button buttonOpenMovies = findViewById(R.id.button_open_movies);
        Button buttonOpenMusic = findViewById(R.id.button_open_music);
        Button buttonOpenBooks = findViewById(R.id.button_open_books);
        Button buttonOpenNew = findViewById(R.id.button_open_new); // NEW BUTTON
        ImageButton buttonOpenProfile = findViewById(R.id.profileButton);
        ImageButton buttonOpenHome = findViewById(R.id.houseButton);
        ImageButton buttonOpenSearch = findViewById(R.id.searchButton);

        // Set click listeners for buttons
        setButtonListeners(buttonOpenMovies, buttonOpenMusic, buttonOpenBooks, buttonOpenNew, buttonOpenProfile,
                buttonOpenHome, buttonOpenSearch);
        new DiscoverMoviesTask().execute();
    }

    protected class DiscoverMoviesTask extends AsyncTask<Void, Void, List<Movie>> {
        @Override
        protected List<Movie> doInBackground(Void... voids) {
            // Crear el interceptor de logging y configurar el nivel de log
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Construir el cliente OkHttpClient e incluir el interceptor de logging
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            List<Movie> movies = new ArrayList<>();
            String baseUrl = "https://api.themoviedb.org/3/movie/popular?language=en-US&sort_by=popularity.desc&page=";
            String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1YzBmM2FmNjcyNjM5YTJjZmUyNmY4NDMyMjk5NjNmNCIsInN1YiI6IjY1ZDg5ZjZiMTQ5NTY1MDE2MmY1YTZhNCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.Io4x374YopHoiG57NIBLZEroKn2vInK1Dzfddkp-ECE";

            for (int page = 1; page <= 3; page++) { // Ajusta según el número de páginas que desees obtener
                Request request = new Request.Builder()
                        .url(baseUrl + page)
                        .get()
                        .addHeader("accept", "application/json")
                        .addHeader("Authorization", token)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray results = jsonObject.getJSONArray("results");

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject movieJson = results.getJSONObject(i);
                        Movie movie = new Movie(
                                movieJson.getInt("id"),
                                movieJson.getString("title"),
                                movieJson.getString("overview"),
                                movieJson.getString("poster_path"),
                                movieJson.getString("vote_average"),
                                movieJson.getString("release_date")
                        );
                        movies.add(movie);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return movies;
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
