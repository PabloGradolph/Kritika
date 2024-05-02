
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

/**
 * Fragment to display a list of new movies.
 */
public class NewMoviesFragment extends Fragment {
    private RecyclerView rvMovies;
    private NewMoviesAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();

    // Required empty public constructor
    public NewMoviesFragment() { }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.new_fragment_movies, container, false);

        rvMovies = view.findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new NewMoviesAdapter(getContext(), movieList);
        adapter.setOnItemClickListener(new NewMoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Intent intent = new Intent(getActivity(), NewMoviesDetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
        rvMovies.setAdapter(adapter);

        new NewMoviesFragment.RetrieveMoviesTask().execute();
        return view;
    }

    /**
     * AsyncTask to retrieve movies from the API.
     */
    protected class RetrieveMoviesTask extends AsyncTask<Void, Void, List<Movie>> {
        @Override
        protected List<Movie> doInBackground(Void... voids) {
            // Create logging interceptor and set log level
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Build OkHttpClient client and include logging interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            List<Movie> movies = new ArrayList<>();
            String baseUrl = ApiConstants.MOVIEDB_POPULAR_MOVIES_URL;
            String token = ApiConstants.MOVIEDB_ACCESS_TOKEN;

            for (int page = 1; page <= 3; page++) {
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
                                movieJson.getString("release_date"),
                                "movies"
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
            }
        }
    }
}
