
package es.uc3m.mobileApps.kritika.newDashboard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import es.uc3m.mobileApps.kritika.model.Movie;
import es.uc3m.mobileApps.kritika.Misc.ApiConstants;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class NewMoviesFragment extends Fragment {
    private RecyclerView rvMovies;
    private NewMoviesAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();

    // Constructor vacío requerido
    public NewMoviesFragment() { }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout para este fragmento
        View view = inflater.inflate(R.layout.new_fragment_movies, container, false);

        rvMovies = view.findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new NewMoviesAdapter(getContext(), movieList);
        // funcionalidad para hacer click
        adapter.setOnItemClickListener(new NewMoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                // En los Fragmentos, usa getActivity() para obtener el contexto de la actividad
                Intent intent = new Intent(getActivity(), NewMoviesDetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });

        rvMovies.setAdapter(adapter);

        // No necesitas los botones de navegación aquí si los gestionas en la actividad principal o a través de un Navigation Component

        // Iniciar la tarea asincrónica para obtener las canciones
        new NewMoviesFragment.RetrieveMoviesTask().execute();

        return view;
    }



    protected class RetrieveMoviesTask extends AsyncTask<Void, Void, List<Movie>> {
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
            String baseUrl = ApiConstants.MOVIEDB_POPULAR_MOVIES_URL;
            String token = ApiConstants.MOVIEDB_ACCESS_TOKEN;

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
